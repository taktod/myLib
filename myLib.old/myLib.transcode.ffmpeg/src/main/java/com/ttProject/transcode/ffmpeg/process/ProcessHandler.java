/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.process;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ttProject.transcode.ffmpeg.FfmpegTranscodeManager;
import com.ttProject.transcode.ffmpeg.worker.DataReceiveWorker;

/**
 * 変換プロセスをコントロールするプログラム
 * 
 * @author taktod
 */
public class ProcessHandler {
	/** ロガー */
	private final Logger logger = Logger.getLogger(ProcessHandler.class);
	/** サーバーにアクセスするためのキー */
	private final String key;
	/** アクセスポート番号 */
	private final int port;
	/** 動作させるコマンド(標準入力でデータを渡して、標準出力に吐く必要あり) */
	private String processCommand = null;
	/** コマンド動作時に追加する追加環境変数 */
	private Map<String, String> envExtra = null;
	/** 動作プロセス */
	private Process process = null;
	/** データ受信処理 */
	private DataReceiveWorker receiveWorker = null;
	/** 主体となるtranscodeManager */
	private final FfmpegTranscodeManager transcodeManager;
	/**
	 * コンストラクタ
	 * @param port
	 */
	public ProcessHandler(FfmpegTranscodeManager transcodeManager, int port) {
		this.transcodeManager = transcodeManager;
		this.port = port;
		this.key = UUID.randomUUID().toString();
	}
	/**
	 * キー参照
	 * @return
	 */
	public String getKey() {
		return key;
	}
	public DataReceiveWorker getReceiveWorker() {
		return receiveWorker;
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
	 * 起動中であるかどうか
	 * @return
	 */
	public boolean isRunning() {
		return process != null;
	}
	/**
	 * プロセスの実行
	 */
	public void executeProcess() throws Exception {
		if(processCommand == null) {
			logger.error("process用のコマンドが存在していないのに、動作させようとしました。");
			throw new Exception("process用のコマンドが設定されていません。");
		}
		// たぶん、動作用のclasspathはjava.class.pathのデータをみればいいと思う。
		StringBuilder command = new StringBuilder();
		command.append("java -Dfile.encoding=UTF-8 -cp").append(" ");
		command.append(System.getProperty("java.class.path")).append(" "); // これがめちゃくちゃ長くなる
		command.append("com.ttProject.transcode.ffmpeg.process.ProcessEntry").append(" ");
		command.append(port).append(" ");
		command.append(key).append(" ");
		command.append("2>java.log");
		command.append(" | ");
		command.append(processCommand);
		logger.info("コマンド:" + processCommand);
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
		logger.info("プロセスを開始しました。");
		// 応答読み取りスレッド
		receiveWorker = new DataReceiveWorker(transcodeManager, 
				process.getInputStream());
		receiveWorker.start();
		logger.info("ここまで");
	}
	public void waitForEnd() throws Exception {
		System.out.println("processHandler");
		if(receiveWorker != null) {
			receiveWorker.waitForEnd();
		}
		if(process != null) {
			process.waitFor();
		}
	}
	/**
	 * 閉じる処理
	 */
	public void close() {
		if(receiveWorker != null) {
			receiveWorker.close();
		}
		// 作成プロセスをこわしておく
		if(process != null) {
			process.destroy();
			process = null;
		}
	}
}
