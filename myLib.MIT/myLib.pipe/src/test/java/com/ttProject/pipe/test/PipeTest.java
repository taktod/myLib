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
 * namePipe work test.
 * @author taktod
 */
public class PipeTest {
	/** logger */
	private Logger logger = Logger.getLogger(PipeTest.class);
	/**
	 * tail test
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		logger.info("start");
		PipeManager manager = new PipeManager();
		final PipeHandler handler = manager.getPipeHandler("tail");
		handler.setCommand("tail -f ${pipe}");
		// execute process.
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
					logger.info("end");
				}
				catch(Exception e) {
					logger.error("get Exception for pipeline response", e);
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
		logger.info("finish");
		ex.shutdownNow(); // kill the execute threads.
	}
}
