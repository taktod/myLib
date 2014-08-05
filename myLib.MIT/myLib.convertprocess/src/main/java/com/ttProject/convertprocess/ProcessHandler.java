/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess;

import java.nio.channels.Channels;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.nio.channels.ReadableByteReadChannel;

/**
 * processの動作の詳細を定義するhandler
 * @author taktod
 */
public class ProcessHandler {
	/** ロガー */
	private static final Logger logger = Logger.getLogger(ProcessHandler.class);
	/** 動作ポート */
	private final int port;
	/** 動作コマンド */
	private String processCommand = null;
	/** 動作環境変数 */
	private Map<String, String> envExtra = null;
	/** 動作プロセス */
	private Process process = null;
	// 子プロセスを指定して動作させるプログラムが必要っぽい。
	private String targetClass = "com.ttProject.convertprocess.server.ProcessEntry";
	/**
	 * コンストラクタ
	 * @param port
	 */
	protected ProcessHandler(int port) {
		this.port = port;
	}
	/**
	 * 動作コマンドを設定する
	 * @param command
	 */
	public void setCommand(String command) {
		this.processCommand = command;
	}
	/**
	 * 起動する子プロセスのクラス名
	 * @param className
	 */
	public void setTargetClass(String className) {
		this.targetClass = className;
	}
	/**
	 * 追加環境変数を設定
	 * @param envExtra
	 */
	public void setEnvExtra(Map<String, String> envExtra) {
		this.envExtra = envExtra;
	}
	/**
	 * 読み込みチャンネルを作成して応答するようにしておく。
	 * @return
	 */
	public IReadChannel getReadChannel() {
		return new ReadableByteReadChannel(Channels.newChannel(process.getInputStream()));
	}
	/**
	 * プロセスを実行
	 * @throws Exception
	 */
	protected void executeProcess() throws Exception {
		if(processCommand == null) {
			logger.error("process用のコマンドが設定されていません。");
			throw new Exception("process用のコマンドが設定されていません。");
		}
		StringBuilder command = new StringBuilder();
		command.append("java -Dfile.encoding=UTF-8 -cp").append(" ");
		command.append(System.getProperty("java.class.path")).append(" "); // これがめちゃくちゃ長くなる
		command.append(targetClass).append(" ");
		command.append(port).append(" ");
		command.append(" 2>/dev/null");
		command.append(" | ");
		command.append(processCommand);
		logger.info("コマンド:" + command.toString());
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		// 環境変数の変更が必要な場合はここでいじっておきます。
		if(envExtra != null) {
			Map<String, String> env = processBuilder.environment();
			for(String key : envExtra.keySet()) {
				String envData = env.get(key);
				if(env == null || "".equals(envData)) {
					envData = envExtra.get(key);
				}
				else {
					envData += ":" + envExtra.get(key);
				}
				env.put(key, envData);
			}
		}
		// プロセスを開始する
		process = processBuilder.start();
		// ここですでにプロセスを起動している。
	}
	/**
	 * 閉じる処理
	 */
	public void close() {
		if(process != null) {
			process.destroy();
			process = null;
		}
	}
}
