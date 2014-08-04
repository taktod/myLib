/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.convertprocess.frame.CodecChecker;
import com.ttProject.convertprocess.frame.CodecType;
import com.ttProject.convertprocess.frame.ShareFrameData;
import com.ttProject.convertprocess.server.ProcessServer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;

/**
 * プロセスにデータを送り出すマネージャー
 * 旧FfmpegConvertManagerに相当
 * @author taktod
 */
public class ProcessManager {
	/** ロガー */
	private static final Logger logger = Logger.getLogger(ProcessManager.class);
	/** 対象プロセス */
	private final Map<String, ProcessHandler> handlers = new HashMap<String, ProcessHandler>();
	/** 動作pid */
	private static final String pid;
	/** 動作ポート番号 */
	private int portNumber;
	/** 動作サーバー */
	private ProcessServer server;
	/** コーデックを判定するchecker */
	private CodecChecker codecChecker = new CodecChecker();
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
	public ProcessManager() throws Exception {
		ProcessServer processServer = null;
		int portNumber = Integer.parseInt(pid) % 1000 + 1000;
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
			logger.fatal("ローカルサーバー用のprocessServerのポート番号が決定できませんでした。");
			throw new RuntimeException("ローカルサーバー用の動作ポート番号が決まらない");
		}
		server = processServer;
		this.portNumber = portNumber;
	}
	/**
	 * 動作プロセスを取得する
	 * @param name
	 * @return
	 */
	public ProcessHandler getProcessHandler(String name) {
		ProcessHandler handler = handlers.get(name);
		if(handler == null) {
			handler = new ProcessHandler(portNumber);
			handlers.put(name, handler);
		}
		return handler;
	}
	/**
	 * 動作を開始する
	 */
	public void start() {
		for(ProcessHandler handler : handlers.values()) {
			try {
				handler.executeProcess();
			}
			catch(Exception e) {
				logger.error("プロセス起動で問題発生", e);
			}
		}
	}
	/**
	 * フレームを子プロセスに送信する
	 * @param frame
	 */
	public void pushFrame(IFrame frame, int id) throws Exception {
		if(frame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame)frame;
			for(IAudioFrame audioFrame : multiFrame.getFrameList()) {
				pushFrame(audioFrame, id);
			}
			return;
		}
		else if(frame instanceof VideoMultiFrame) {
			VideoMultiFrame multiFrame = (VideoMultiFrame)frame;
			for(IVideoFrame videoFrame : multiFrame.getFrameList()) {
				pushFrame(videoFrame, id);
			}
			return;
		}
		if(frame == null) {
			// frameがnullの場合はほっとく。
			return;
		}
		// codecCheckerを利用して、どのcodecTypeであるか調べる
		CodecType codecType = codecChecker.checkCodecType(frame);
		// shareFrameDataを作り出しておく
		ShareFrameData shareFrameData = new ShareFrameData(codecType, frame, id);
		// 送るデータをセットしておく。
		ByteBuffer buffer = null;
		switch(codecType) {
		case H264:
			buffer = frame.getPackBuffer();
			break;
		default:
			buffer = frame.getData();
			break;
		}
		if(buffer == null) {
			return;
		}
		shareFrameData.setFrameData(buffer);
		// 相手に送付する。
		server.sendData(shareFrameData.getShareData());
	}
	/**
	 * 終了処理
	 */
	public void close() {
		for(ProcessHandler handler : handlers.values()) {
			handler.close();
		}
		if(server != null) {
			server.closeServer();
		}
	}
}
