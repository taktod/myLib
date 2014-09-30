/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx.test;

import javax.management.Notification;

import org.apache.log4j.Logger;
import org.junit.Assert;

import com.ttProject.jmx.JMXFactory;
import com.ttProject.jmx.JMXServerFactory;

/**
 * notification test
 * @author taktod
 */
public class JMXNotificationTest {
	/** logger */
	private static Logger logger = Logger.getLogger(JMXNotificationTest.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("start(remote + notification)");
		try {
			TestMXBean mxBean = new TestMXBean();
			// open port with processId#s
			JMXServerFactory.openJMXRemoteServer();
			logger.info("jmx port:" + JMXServerFactory.getPort());
			JMXFactory.setDomain("com.test.control:type=");
			JMXFactory.registerMBean("control", mxBean);
			while(mxBean.isWork()) {
				Notification n = new Notification("test", mxBean, 0, System.currentTimeMillis(), "send message.");
				mxBean.sendNotification(n);
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
