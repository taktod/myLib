/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ttProject.convertprocess.server.DataSendWorker;
import com.ttProject.convertprocess.server.ProcessServer;

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
	/** データ転送用thread(executorにしたい) */
	private Thread thread = null;
	/** データ送信処理 */
	private DataSendWorker worker = null;
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
			}
			catch(Exception e) {
				;
			}
		}
		if(portNumber > 65535) {
			logger.fatal("ローカルサーバー用のprocessServerのポート番号が決定できませんでした。");
			throw new RuntimeException("ローカルサーバー用の動作ポート番号が決まらない");
		}
	}
}
