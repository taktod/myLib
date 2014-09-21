/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.pipe.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.ttProject.pipe.PipeHandler;
import com.ttProject.pipe.PipeManager;

/**
 * 名前付きpipeでなにかする動作テスト
 * @author taktod
 */
public class PipeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(PipeTest.class);
	/**
	 * 動作テスト
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		logger.info("動作テスト");
		// とりあえず、managerをつくる。
		PipeManager manager = new PipeManager();
		final PipeHandler handler = manager.getPipeHandler("tail");
		handler.setCommand("tail -f ${pipe}");
		handler.executeProcess();
		ExecutorService ex = Executors.newCachedThreadPool();
		ex.execute(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(handler.getInputStream()));
					String line = null;
					while((line = reader.readLine()) != null) {
						logger.info(line);
					}
					logger.info("おわった");
				}
				catch(Exception e) {
					logger.error("データ取得スレッドで例外が発生しました。", e);
				}
			}
		});
		PrintWriter writer = new PrintWriter(handler.getPipeTarget());
		writer.println("test");
		writer.flush();
		writer.println("aiueo");
		writer.flush();
		writer.close();
		Thread.sleep(1000);
		logger.info("おしまい");
		ex.shutdownNow();
	}
}
