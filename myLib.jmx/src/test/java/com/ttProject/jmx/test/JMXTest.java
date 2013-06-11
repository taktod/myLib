package com.ttProject.jmx.test;

import com.ttProject.jmx.JMXFactory;

/**
 * jmxの動作
 * @author taktod
 */
public class JMXTest {
	/**
	 * エントリー
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("動作開始");
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
		}
	}
}
