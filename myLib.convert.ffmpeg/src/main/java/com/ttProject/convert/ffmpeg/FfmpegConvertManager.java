package com.ttProject.convert.ffmpeg;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ttProject.convert.IConvertManager;
import com.ttProject.convert.ffmpeg.process.ProcessServer;

/**
 * ffmpegやavconvを利用してメディアデータを変換するプログラム
 * xuggleによる動作とちがって複数同時コンバートや、コンバートのデータの共有とかはできない。
 * ただしマルチスレッド動作ができるので、パフォーマンスは良い
 * @author taktod
 */
public class FfmpegConvertManager implements IConvertManager {
	private final Logger logger = LoggerFactory.getLogger(FfmpegConvertManager.class);
	private final Map<String, ProcessHandler> handlers = new HashMap<String, ProcessHandler>();
	private static final String pid;
	private final int portNumber;
	private final ProcessServer server;
	private Thread dataSendingThread = null;
	private LinkedBlockingQueue<ChannelBuffer> dataQueue = new LinkedBlockingQueue<ChannelBuffer>();
	static {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		pid = bean.getName().split("@")[0];
	}
	public FfmpegConvertManager() throws Exception {
		// このタイミングでサーバーを起動していた方がいいかも・・・
		// このタイミングでサーバーをつくる
		ProcessServer ps = null;
		int portNumber = Integer.parseInt(pid);
		if(portNumber < 1000) {
			portNumber += 1000;
		}
		for(;portNumber < 65535;portNumber += 1000) {
			try {
				ps = new ProcessServer(portNumber);
				break;
			}
			catch(Exception e) {
				;
			}
		}
		if(portNumber > 65535) {
			throw new RuntimeException("ローカルサーバーの動作ポート番号が決まりませんでした。");
		}
		server = ps;
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
					while(true) {
						ChannelBuffer buffer = dataQueue.take();
						server.sendData(buffer);
					}
				}
				catch (Exception e) {
					logger.info("データ送信動作がとまりました。", e);
				}
			}
		});
		dataSendingThread.setDaemon(true);
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
		if(dataQueue != null) {
			dataQueue.clear();
			dataQueue = null;
		}
	}
}
