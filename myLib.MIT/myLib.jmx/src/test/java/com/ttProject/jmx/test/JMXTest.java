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
 * jmx test
 * @author taktod
 */
public class JMXTest {
	/** logger */
	private static Logger logger = Logger.getLogger(JMXTest.class);
	/**
	 * entry
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("start");
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
			Assert.fail("Exception occured");
		}
	}
}
