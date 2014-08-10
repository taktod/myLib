/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.pipe;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 名前付きpipeをlinuxやmacで利用するためのマネージャー
 * @author taktod
 */
public class PipeManager {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(PipeManager.class);
	/** 動作対象プロセス */
	private final Map<String, PipeHandler> handlers = new HashMap<String, PipeHandler>();
	/** 実行プロセスID */
	private static final String pid;
	/**
	 * 静的初期化
	 */
	static {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		pid = bean.getName().split("@")[0];
	}
	/**
	 * コンストラクタ
	 */
	public PipeManager() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// shutdown時の処理
				// 動作させたプロセスはすべて殺しておく
				for(PipeHandler handler : handlers.values()) {
					handler.close();
				}
			}
		});
	}
	/**
	 * pipeHandlerを参照する
	 * @param name
	 * @return
	 */
	public synchronized PipeHandler getPipeHandler(String name) {
		PipeHandler handler = handlers.get(name);
		if(handler == null) {
			handler = new PipeHandler(name, pid);
			handlers.put(name, handler);
		}
		return handler;
	}
}
