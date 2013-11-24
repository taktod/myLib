package com.ttProject.transcode.ffmpeg.worker;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

/**
 * データの受信処理
 * @author taktod
 */
public class DataReceiveWorker implements Runnable {
	/** 動作ロガー */
	private final Logger logger = Logger.getLogger(DataReceiveWorker.class);
	/** 動作読み込みチャンネル */
	private final ReadableByteChannel outputChannel;
	/** 処理転送先listener */
	private boolean workFlg = true;
	/** 動作させるexecutor */
	private ExecutorService loopExecutor = null;
	/**
	 * コンストラクタ
	 * @param outputChannel
	 */
	public DataReceiveWorker(ReadableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	/**
	 * 処理に利用するexecutorを設定
	 * @param executor
	 */
	public void setExecutor(ExecutorService executor) {
		loopExecutor = executor;
	}
	/**
	 * 開始します
	 */
	public void start() {
		if(loopExecutor == null) {
			loopExecutor = Executors.newSingleThreadExecutor();
		}
		loopExecutor.execute(this);
	}
	/**
	 * 動作実体
	 */
	@Override
	public void run() {
		try {
			boolean readFlg = false;
			ByteBuffer buffer = ByteBuffer.allocate(65536);
			outputChannel.read(buffer);
			buffer.flip();
			readFlg = buffer.remaining() != 0;
			// bufferはここから適当なリスナーに渡す必要あり。
			logger.info("size:" + buffer.remaining());
			if(workFlg) {
				if(!readFlg) {
					if(loopExecutor instanceof ThreadPoolExecutor) {
						ThreadPoolExecutor threadExecutor = (ThreadPoolExecutor) loopExecutor;
						if(threadExecutor.getQueue().size() == 0) {
							// queueのデータがない場合はちょっと待つ
							Thread.sleep(100);
						}
					}
				}
				// 再度実行させる
				loopExecutor.execute(this);
			}
		}
		catch(Exception e) {
			logger.error("例外が発生して、読み込みスレッドがとまりました。", e);
		}
	}
}
