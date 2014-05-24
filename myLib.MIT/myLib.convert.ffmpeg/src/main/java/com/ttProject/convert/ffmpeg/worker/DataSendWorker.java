/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convert.ffmpeg.worker;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.ttProject.convert.ffmpeg.process.ProcessServer;

/**
 * データを送り込むworker
 * @author taktod
 */
public class DataSendWorker implements Runnable {
	/** 動作ロガー */
	private static final Logger logger = Logger.getLogger(DataSendWorker.class);
	/** 動作サーバー */
	private final ProcessServer server;
	/** 処理開始用のロックオブジェクト */
	private final Set<String> keySet;
	/** 転送データソース */
	private final LinkedBlockingQueue<ChannelBuffer> dataQueue;
	/** 動作フラグ */
	private boolean workFlg = true;
	/**
	 * コンストラクタ
	 * @param server
	 * @param dataQueue
	 */
	public DataSendWorker(ProcessServer server, LinkedBlockingQueue<ChannelBuffer> dataQueue) {
		this.server = server;
		this.keySet = server.getKeySet();
		this.dataQueue = dataQueue;
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
			synchronized(keySet) {
				// 必要なサーバーからのアクセスが完了したらnotifyされて、実行開始になります。
				keySet.wait();
			}
			while(workFlg) {
				ChannelBuffer buffer = dataQueue.take();
				server.sendData(buffer);
			}
		}
		catch (InterruptedException e) {
			// 処理中断させれただけなので放置します。
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
		}
	}
}
