package com.ttProject.jmx.test;

import org.junit.Test;

import com.ttProject.jmx.JMXFactory;
import com.ttProject.jmx.JMXServerFactory;

@SuppressWarnings("unused")
public class JMXTest {
	public static final String TEST_SERVER_ADDRESS = 
			"service:jmx:rmi:///jndi/rmi://localhost/trivia";
//	@Test
	public void test() throws Exception {
		// テスト動作をやってみる。
		JMXServerFactory.openJMXRemoteServer(3000);
		// MXBeanとそれに追随するIMXBeanBaseのインターフェイスがあれば、それをJconsole経由で呼び出し可能になります。
		JMXFactory.registerMBean("testhogehoge", new TestMXBean());
		while(true) {
			Thread.sleep(1000);
		}
	}
}
