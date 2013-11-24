package com.ttProject.transcode.ffmpeg.worker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ReadableByteChannel;

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
	/**
	 * コンストラクタ
	 * @param outputChannel
	 */
	public DataReceiveWorker(ReadableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	/**
	 * 動作実体
	 */
	@Override
	public void run() {
		try {
			while(workFlg) {
				ByteBuffer buffer = ByteBuffer.allocate(65536);
				// 処理入力を読み込みます。
				outputChannel.read(buffer);
				buffer.flip();
				// リスナーにデータを渡す
				// CPUをちょっとだけ解放しておく。// (おわったら次のexecutorServiceに処理を依頼する・・・でいけるか)
				Thread.sleep(10);
			}
		}
		catch(ClosedByInterruptException e) {
			
		}
		catch(IOException e) {
			
		}
		catch(InterruptedException e) {
			
		}
		catch(Exception e) {
			logger.error("想定外の例外が発生しました。", e);
		}
		// ここでやること。
		// ffmpegの標準出力を受け取るので、必要なunitに戻して応答してやる。
	}
}
