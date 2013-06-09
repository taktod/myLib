package com.ttProject.jmx.test;

import com.ttProject.jmx.JMXFactory;
import com.ttProject.jmx.JMXServerFactory;

/**
 * jmxのremote接続用のテスト
 * @author taktod
 */
public class JMXRemoteTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("動作開始(remoteバージョン)");
		try {
			TestMXBean mxBean = new TestMXBean();
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
		}
	}
}
