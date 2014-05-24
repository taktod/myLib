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

/**
 * jmxの動作
 * @author taktod
 */
public class JMXTest {
	/** 動作ロガー */
	private static Logger logger = Logger.getLogger(JMXTest.class);
	/**
	 * エントリー
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("動作開始");
		TestMXBean mxBean = new TestMXBean();
		JMXFactory.registerMBean("control", mxBean);
		try {
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
