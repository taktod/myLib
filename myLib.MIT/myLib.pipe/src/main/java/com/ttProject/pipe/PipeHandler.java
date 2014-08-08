/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.pipe;

import java.io.File;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.nio.channels.ReadableByteReadChannel;

/**
 * pipeの処理を実施するhandler
 * @author taktod
 *
 */
public class PipeHandler {
	/** ロガー */
	private Logger logger = Logger.getLogger(PipeHandler.class);
	/** namedpipeで利用する名前 */
	private final String name;
	/** pipeの位置 */
	private final String namedPipe;
	/** 動作コマンド */
	private String processCommand;
	/** 追加の環境変数 */
	private Map<String, String> envExtra = null;
	/** 実行プロセス */
	private Process process = null;
	/**
	 * コンストラクタ
	 */
	public PipeHandler(String name, String pid) {
		this.name = name;
		this.namedPipe = System.getProperty("java.io.tmpdir") + "myLib.pipe/" + name + "_" + pid;
	}
	/**
	 * 実行コマンド ${pipe}の部分にtargetが入ります。
	 * @param command
	 */
	public void setCommand(String command) {
		this.processCommand = command;
	}
	/**
	 * 追加の環境変数設定
	 * @param envExtra
	 */
	public void setEnvExtra(Map<String, String> envExtra) {
		this.envExtra = envExtra;
	}
	/**
	 * 名前付きパイプのfileを応答する
	 * @return
	 */
	public File getPipeTarget() {
		return new File(namedPipe);
	}
	/**
	 * pipeの名称を参照する
	 * @return
	 */
	public String getPipeName() {
		return name;
	}
	/**
	 * 実行動作
	 */
	protected void executeProcess() throws Exception {
		if(processCommand == null) {
			logger.error("process動作用のコマンドが存在していないのに、動作させようとしました。");
			throw new Exception("process用のコマンドが設定されていません。");
		}
		setupPipe();
		
		StringBuilder command = new StringBuilder();
		command.append(processCommand.replaceAll("\\$\\{pipe\\}", namedPipe));
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command.toString());
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
		process = processBuilder.start();
	}
	/**
	 * pipeを作成する。
	 */
	private void setupPipe() throws Exception {
		File f = new File(namedPipe);
		f.getParentFile().mkdirs();
		f.delete();
		StringBuilder command = new StringBuilder();
		command.append("mkfifo " + namedPipe);
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		Process p = builder.start();
		p.waitFor();
	}
	/**
	 * 読み込みチャンネルを応答する
	 * @return
	 */
	public IReadChannel getReadChannel() throws Exception {
		if(process == null) {
			throw new Exception("プロセスが実行していません。");
		}
		return new ReadableByteReadChannel(Channels.newChannel(process.getInputStream()));
	}
	/**
	 * 標準入力ストリームを参照する
	 * @return
	 * @throws Exception
	 */
	public InputStream getInputStream() throws Exception {
		if(process == null) {
			throw new Exception("プロセスが実行していません。");
		}
		return process.getInputStream();
	}
	/**
	 * 終了処理
	 */
	public void close() {
		// プロセスを殺しておく。
		if(process != null) {
			process.destroy();
		}
		// pipeを消しておく。
		new File(namedPipe).delete();
	}
}
