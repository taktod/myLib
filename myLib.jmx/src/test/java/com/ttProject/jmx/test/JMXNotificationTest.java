package com.ttProject.jmx.test;

import javax.management.Notification;

import org.junit.Assert;

import com.ttProject.jmx.JMXFactory;
import com.ttProject.jmx.JMXServerFactory;

/**
 * 通知動作テスト
 * @author taktod
 */
public class JMXNotificationTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("動作開始(remote + 通知バージョン)");
		try {
			TestMXBean mxBean = new TestMXBean();
			// processIdをベースに開きます。
			JMXServerFactory.openJMXRemoteServer();
			System.out.println("jmx port:" + JMXServerFactory.getPort());
			JMXFactory.setDomain("com.test.control:type=");
			JMXFactory.registerMBean("control", mxBean);
			while(mxBean.isWork()) {
				Notification n = new Notification("test", mxBean, 0, System.currentTimeMillis(), "メッセージを送ります。");
				mxBean.sendNotification(n);
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
