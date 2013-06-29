package com.ttProject.convert.ffmpeg.worker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ReadableByteChannel;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.convert.IConvertListener;

/**
 * データを受け取るworker
 * @author taktod
 */
public class DataReceiveWorker implements Runnable {
	/** 動作ロガー */
	private static final Logger logger = Logger.getLogger(DataReceiveWorker.class);
	/** 動作読み込みチャンネル */
	private final ReadableByteChannel outputChannel;
	/** 処理転送先listener一覧 */
	private final Set<IConvertListener> listeners;
	/** 動作フラグ */
	private boolean workFlg = true;
	/**
	 * コンストラクタ
	 * @param outputChannel
	 * @param listeners
	 */
	public DataReceiveWorker(ReadableByteChannel outputChannel, Set<IConvertListener> listeners) {
		this.outputChannel = outputChannel;
		this.listeners = listeners;
	}
	/**
	 * 停止処理
	 */
	public void stop() {
		workFlg = false;
	}
	/**
	 * 実行部
	 */
	@Override
	public void run() {
		try {
			while(workFlg) {
				ByteBuffer buffer = ByteBuffer.allocate(65536);
				// 処理入力を読み込みます。
				outputChannel.read(buffer);
				buffer.flip();
				// listenerに処理を依頼します。
				for(IConvertListener listener : listeners) {
					listener.receiveData(buffer);
				}
				// cpuをあけるために、0.01秒CPUを解放しておきます。
				Thread.sleep(10);
			}
		}
		catch (ClosedByInterruptException e) {
			// IOExceptionに丸めてもいいかも
		}
		catch (IOException e) {
			// stream.closeというのがくる(outputChannelに対するもの)
		}
		catch (InterruptedException e) {
			// 処理中断させれただけなので放置します。
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
		}
	}
}
