/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convert.ffmpeg;

import java.nio.channels.Channels;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ttProject.convert.IConvertListener;
import com.ttProject.convert.ffmpeg.worker.DataReceiveWorker;

/**
 * ffmpegConvertManagerで利用する内部プロセスのハンドルクラス
 * @author taktod
 */
public class ProcessHandler {
	/** ロガー */
	private static final Logger logger = Logger.getLogger(ProcessHandler.class);
	/** サーバーにアクセスするためのキー */
	private final String key;
	/** アクセスポート番号 */
	private final int port;
	/** 子プロセスの標準出力受付thread */
	private Thread thread = null;
	/** 動作させるコマンド(標準入力でデータを渡して、標準出力に吐く必要あり) */
	private String processCommand = null;
	/** コマンド動作時に追加する追加環境変数 */
	private Map<String, String> envExtra = null;
	/** データ受け取り時のイベント処理 */
	private final Set<IConvertListener> listeners = new HashSet<IConvertListener>();
	/** 動作プロセス */
	private Process process = null;
	/** データ受信処理 */
	private DataReceiveWorker worker = null;
	/**
	 * コンストラクタ
	 * @param port
	 */
	protected ProcessHandler(int port) {
		this.port = port;
		this.key = UUID.randomUUID().toString();
	}
	/**
	 * キー参照
	 * @return
	 */
	protected String getKey() {
		return key;
	}
	/**
	 * 動作コマンドを設定する
	 * @param command
	 */
	public void setCommand(String command) {
		this.processCommand = command;
	}
	/**
	 * 追加環境変数を設定
	 * @param envExtra
	 */
	public void setEnvExtra(Map<String, String> envExtra) {
		this.envExtra = envExtra;
	}
	/**
	 * データ受け取り時の処理listenerの追加
	 * @param listener
	 */
	public void addListener(IConvertListener listener) {
		listeners.add(listener);
	}
	/**
	 * データ受け取り時の処理listenerの除去
	 * @param listener
	 */
	public void removeListener(IConvertListener listener) {
		listeners.remove(listener);
	}
	/**
	 * プロセスの実行
	 */
	protected void executeProcess() throws Exception {
		if(processCommand == null) {
			logger.error("process用のコマンドが存在していないのに、動作させようとしました。");
			throw new Exception("process用のコマンドが設定されていません。");
		}
		// たぶん、動作用のclasspathはjava.class.pathのデータをみればいいと思う。
		StringBuilder command = new StringBuilder();
		command.append("java -Dfile.encoding=UTF-8 -cp").append(" ");
		command.append(System.getProperty("java.class.path")).append(" "); // これがめちゃくちゃ長くなる
		command.append("com.ttProject.convert.ffmpeg.process.ProcessEntry").append(" ");
		command.append(port).append(" ");
		command.append(key).append(" ");
		command.append("2>/dev/null");
		command.append(" | ");
		command.append(processCommand);
		logger.info("コマンド:" + processCommand);
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
		// 応答読み取りスレッド
		worker = new DataReceiveWorker(Channels.newChannel(process.getInputStream()), listeners);
		thread = new Thread(worker);
//		thread.setDaemon(true);
		thread.setName("ffmpegDataReceiveThread");
		thread.start();
	}
	/**
	 * 閉じる処理
	 */
	public void close() {
		// 管理リスナーを解放しておく。
		listeners.clear();
		// 内部threadの処理を抜けさせる
		if(worker != null) {
			worker.stop();
		}
		// Threadの解放
		if(thread != null) {
			thread.interrupt();
			thread = null;
		}
		// 作成プロセスをこわしておく
		if(process != null) {
			process.destroy();
			process = null;
		}
	}
}
