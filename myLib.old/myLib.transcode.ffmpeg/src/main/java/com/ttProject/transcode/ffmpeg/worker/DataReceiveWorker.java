/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.worker;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import com.ttProject.transcode.ffmpeg.FfmpegTranscodeManager;

/**
 * データの受信処理
 * @author taktod
 */
public class DataReceiveWorker implements Runnable {
	/** 動作ロガー */
	private final Logger logger = Logger.getLogger(DataReceiveWorker.class);
	/** 動作読み込みstream */
	private final InputStream is;
	/** 動作読み込みチャンネル */
	private final ReadableByteChannel outputChannel;
	/** 処理転送先listener */
	private boolean workFlg = true;
	/** 動作させるexecutor */
	private ExecutorService loopExecutor = null;
	/** データを戻す処理本体 */
	private final FfmpegTranscodeManager transcodeManager;
	/** 処理終了フラグ(このフラグが立っている状態で1秒データ転送がなければ殺します) */
	private boolean endFlg = false;
	/** 最終処理時刻 */
	private long lastTaskTime = -1;
	/**
	 * コンストラクタ
	 * @param outputChannel
	 */
	public DataReceiveWorker(FfmpegTranscodeManager transcodeManagaer, InputStream is) {
		this.transcodeManager = transcodeManagaer;
		this.is = is;
		this.outputChannel = Channels.newChannel(is);
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
	public void waitForEnd() {
		System.out.println("おわりがみえた!?");
		endFlg = true;
	}
	/**
	 * 停止処理
	 */
	public void close() {
		
	}
	/**
	 * 動作実体
	 */
	@Override
	public void run() {
		try {
			boolean readFlg = false;
			if(is.available() != 0) {
				ByteBuffer buffer = ByteBuffer.allocate(65536);
				outputChannel.read(buffer); // このreadがlockするらしい。
				buffer.flip();
				readFlg = buffer.remaining() != 0;
				if(readFlg) {
					lastTaskTime = System.currentTimeMillis();
				}
				// unit化を実行
				transcodeManager.process(transcodeManager.getUnitizer().getUnits(buffer));
			}
			if(endFlg && System.currentTimeMillis() - lastTaskTime > 1000) {
				// 強制的におわらせます。
				logger.info("データがこなくなってから1秒たったので、とめます。");
				transcodeManager.close();
				workFlg = false;
			}
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
