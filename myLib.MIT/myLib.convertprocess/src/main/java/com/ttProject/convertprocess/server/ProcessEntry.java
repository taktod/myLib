/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.server;

import org.apache.log4j.Logger;

import com.ttProject.convertprocess.frame.IShareFrameListener;
import com.ttProject.frame.IFrame;

/**
 * データを受信して、標準出力として、プロセスにデータを渡すprocessのエントリーポイント
 * 実際はflv用、mkv用等・・・いろいろと派生をつくることになる予定
 * @author taktod
 */
public class ProcessEntry implements IShareFrameListener {
	/** 動作ロガー */
	private static Logger logger = Logger.getLogger(ProcessEntry.class);
	private ProcessClient client = null;
	/**
	 * メインエントリー
	 * @param args
	 */
	public static void main(String args[]) {
		if(args == null || args.length != 1) { // キーがなくなったので、１つになっているのが問題っぽい。
			for(String data : args) {
				logger.info(data);
			}
			logger.warn("引数の数がおかしいです。");
			System.exit(-1);
			return;
		}
		// ポート番号を指定して、アクセスしなければいけない。
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
			System.err.println("入力ポート番号の数値解釈できませんでした。");
			System.exit(-1);
			return;
		}
		ProcessEntry entry = new ProcessEntry();
		entry.start(port);
	}
	/**
	 * コンストラクタ
	 */
	public ProcessEntry() {
		client = new ProcessClient(this);
	}
	/**
	 * フレームを取得したときの動作
	 */
	@Override
	public void pushFrame(IFrame frame, int id) {
		logger.info(id + " " + frame.toString());
	}
	/**
	 * クライアントアクセス開始
	 */
	public void start(int port) {
		// 接続を開始する。
		client.connect("localhost", port);
	}
}
