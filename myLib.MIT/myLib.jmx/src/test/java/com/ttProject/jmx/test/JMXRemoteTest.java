/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx.test;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.ttProject.jmx.JMXFactory;
import com.ttProject.jmx.JMXServerFactory;

/**
 * jmxのremote接続用のテスト
 * @author taktod
 */
public class JMXRemoteTest {
	/** 動作ロガー */
	private static Logger logger = Logger.getLogger(JMXRemoteTest.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("動作開始(remoteバージョン)");
		try {
			TestMXBean mxBean = new TestMXBean();
			// ポート番号を指定して開きます。
			JMXServerFactory.openJMXRemoteServer(12345);
			JMXFactory.setDomain("com.test.control:type=");
			JMXFactory.registerMBean("control", mxBean);
			while(mxBean.isWork()) {
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException e) {
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
	}
}
