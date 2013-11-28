package com.ttProject.transcode.ffmpeg.worker;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ffmpeg.FfmpegTranscodeManager;
import com.ttProject.transcode.ffmpeg.process.ProcessServer;

/**
 * データを送り込みます。
 * ffmpegのプロセスが受け入れ完了してからデータを送信する必要がある。
 * @author taktod
 */
public class DataSendWorker implements Runnable {
	/** 動作ロガー */
	private final Logger logger = Logger.getLogger(DataSendWorker.class);
	/** 動作サーバー */
	private final ProcessServer server;
	/** 転送データソース */
	private final LinkedBlockingQueue<Unit> dataQueue = new LinkedBlockingQueue<Unit>();
	/** 動作させるexecutor */
	private ExecutorService loopExecutor = null;
	private final FfmpegTranscodeManager transcodeManager;
	/** 動作フラグ */
	private boolean started = false;
	/**
	 * コンストラクタ
	 * @param server
	 */
	public DataSendWorker(FfmpegTranscodeManager transcodeManager, ProcessServer server) {
		this.transcodeManager = transcodeManager;
		this.server = server;
	}
	/**
	 * 処理に利用するexecutorを設定
	 * @param executor
	 */
	public void setExecutor(ExecutorService executor) {
		loopExecutor = executor;
	}
	/**
	 * serverから開始準備ができたらキックされる動作
	 */
	public synchronized void start() {
		if(loopExecutor == null) {
			loopExecutor = Executors.newSingleThreadExecutor();
		}
		loopExecutor.execute(this);
		started = true;
	}
	/**
	 * 停止処理
	 */
	public void close() {
		// たまっているデータをすべて吐き出して、終わらせる必要あり
	}
	/**
	 * データを転送します
	 * ここにデータをいれれば順次転送されていく・・・というのがいい。
	 * とりあえずロックかけて、同期処理になるようにしておく。
	 */
	public synchronized void send(Unit unit) throws Exception {
		if(!transcodeManager.getDeunitizer().check(unit)) {
			return;
		}
		if(!started) {
			// 開始前はqueueにいれていくだけ。
			dataQueue.add(unit);
		}
		else if(dataQueue.size() > 0) {
			// queueにデータがはいっている場合も追加するだけ
			dataQueue.add(unit);
		}
		else {
			// queueにデータがなくて、開始済みの場合は、queueにいれて、executorを実行する。
			dataQueue.add(unit); // いれて
			loopExecutor.execute(this); // 実行
		}
	}
	/**
	 * 実行処理
	 */
	@Override
	public void run() {
		try {
			// 個々別にwaitかけなくても、serverがデータうけとったら、threadの動作開始すれば済むっぽい。
			// dataQueueに入っているデータがなくなるまで、実行しつづける。
			// どっちかというと、無限ループより、executor呼び合いの方がいいか・・・
			Unit unit = dataQueue.poll(); // 即実行
			// 送るbufferを作成する。
			ByteBuffer buffer = transcodeManager.getDeunitizer().getBuffer(unit);
			// clientにデータを送る。
			if(buffer != null) {
				server.sendData(ChannelBuffers.copiedBuffer(buffer));
			}
			if(dataQueue.size() > 0) {
				// データがあったら再度実行する
				loopExecutor.execute(this);
			}
		}
		catch(Exception e) {
			logger.error("想定外の例外が発生しました。", e);
		}
	}
}
