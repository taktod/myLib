package com.ttProject.convert.ffmpeg;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.ttProject.convert.IConvertManager;
import com.ttProject.convert.ffmpeg.process.ProcessServer;

/**
 * ffmpegやavconvを利用してメディアデータを変換するプログラム
 * xuggleによる動作とちがって複数同時コンバートや、コンバートのデータの共有とかはできない。
 * ただしマルチスレッド動作ができるので、パフォーマンスは良い
 * @author taktod
 */
public class FfmpegConvertManager implements IConvertManager {
	/** 動作ロガー */
	private static final Logger logger = Logger.getLogger(FfmpegConvertManager.class);
	/** 対象プロセス */
	private final Map<String, ProcessHandler> handlers = new HashMap<String, ProcessHandler>();
	/** 動作pid */
	private static final String pid;
	/** 動作ポート番号 */
	private final int portNumber;
	/** 動作サーバー */
	private final ProcessServer server;
	/** データ転送用のスレッド */
	private Thread dataSendingThread = null;
	/** 稼働中フラグ */
	private boolean workFlg = true;
	/** データ転送用のqueue */
	private LinkedBlockingQueue<ChannelBuffer> dataQueue = new LinkedBlockingQueue<ChannelBuffer>();
	/**
	 * 静的初期化
	 */
	static {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		pid = bean.getName().split("@")[0];
	}
	/**
	 * コンストラクタ
	 * @throws Exception
	 */
	public FfmpegConvertManager() throws Exception {
		// このタイミングでサーバーを起動していた方がいいかも・・・
		// このタイミングでサーバーをつくる
		ProcessServer processServer = null;
		// 処理可能なポート番号をみつける必要がある。
		int portNumber = Integer.parseInt(pid);
		if(portNumber < 1000) {
			portNumber += 1000;
		}
		for(;portNumber < 65535;portNumber += 1000) {
			try {
				processServer = new ProcessServer(portNumber);
				break;
			}
			catch(Exception e) {
				;
			}
		}
		if(portNumber > 65535) {
			logger.fatal("プロセス番号ベースでローカルサーバー用のポート番号が見つけられませんでした。");
			throw new RuntimeException("ローカルサーバーの動作ポート番号が決まりませんでした。");
		}
		// データが決まったので処理を続ける
		server = processServer;
		this.portNumber = portNumber;
		// threadをつくってデータを送る
		dataSendingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Set<String> keySet = server.getKeySet();
				try {
					synchronized(keySet) {
						keySet.wait();
					}
					while(workFlg) {
						ChannelBuffer buffer = dataQueue.take();
						server.sendData(buffer);
					}
				}
				catch (InterruptedException e) {
					// 停止処理になっただけなので放置しておく。
				}
				catch (Exception e) {
					logger.info("データ送信動作がとまりました。", e);
				}
			}
		});
		dataSendingThread.setDaemon(true);
		dataSendingThread.setName("ffmpegDataSendingThread");
		dataSendingThread.start();
	}
	/**
	 * 動作handlerを取得する
	 */
	public ProcessHandler getProcessHandler(String name) {
		ProcessHandler handler = handlers.get(name);
		if(handler == null) {
			handler = new ProcessHandler(portNumber);
			handlers.put(name, handler);
			server.addKey(handler.getKey());
		}
		return handler;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		for(ProcessHandler handler : handlers.values()) {
			try {
				handler.executeProcess();
			}
			catch (Exception e) {
				logger.error("プロセス起動で問題発生", e);
			}
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applyData(ByteBuffer buffer) {
		// queueにデータをいれていく。
		dataQueue.add(ChannelBuffers.copiedBuffer(buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		workFlg = false;
		for(String key : handlers.keySet()) {
			handlers.get(key).close();
		}
		// サーバーを閉じておく
		if(server != null) {
			server.closeServer();
		}
		// データ転送threadを閉じておく。
		if(dataSendingThread != null) {
			dataSendingThread.interrupt();
			dataSendingThread = null;
		}
		// 処理データqueueを閉じておく
		if(dataQueue != null) {
			dataQueue.clear();
			dataQueue = null;
		}
	}
}
