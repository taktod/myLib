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
 * jmx remote connection test
 * @author taktod
 */
public class JMXRemoteTest {
	/** logger */
	private static Logger logger = Logger.getLogger(JMXRemoteTest.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("jmxtest(remote)");
		try {
			TestMXBean mxBean = new TestMXBean();
			// open with fixed port number.
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
			Assert.fail("Exception occured");
		}
	}
}
