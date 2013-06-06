package com.ttProject.jmx;

import java.rmi.registry.LocateRegistry;

import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

/**
 * JMX用のリモートサーバーをたてます。
 * @author taktod
 */
public class JMXServerFactory {
	/** 動作ロガー */
	private static final Logger logger = Logger.getLogger(JMXServerFactory.class);
	/**
	 * jmxのリモート用のサーバーをたてておきます
	 * @param port たてるサーバーのポート番号
	 */
	public static void openJMXRemoteServer(int port) {
		try {
			LocateRegistry.createRegistry(port);
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
			JMXConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url, null, JMXFactory.getMBeanServer());
			server.start();
		}
		catch (Exception e) {
			logger.error("jmxのリモートサーバーを開くときに失敗しました。", e);
		}
	}
}
