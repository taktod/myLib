/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.rmi.registry.LocateRegistry;

import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * JMX用のリモートサーバーをたてます。
 * @author taktod
 */
public class JMXServerFactory {
	/** jmxサーバーとして利用しているポート */
	private static int port = -1;
	/**
	 * 動作ポート番号参照
	 * @return
	 */
	public static int getPort() {
		return port;
	}
	/**
	 * jmxのリモート用のサーバーをたてておきます
	 * @param port たてるサーバーのポート番号
	 */
	public static void openJMXRemoteServer(int port) throws Exception {
		JMXServerFactory.port = port;
		LocateRegistry.createRegistry(port);
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
		JMXConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url, null, JMXFactory.getMBeanServer());
		server.start();
	}
	/**
	 * jmxのリモート用のサーバーをたてておきます。
	 * ポート番号はpid + 1000xとしておきます。
	 * @throws Exception
	 */
	public static void openJMXRemoteServer() throws Exception {
		if(JMXServerFactory.port != -1) {
			// すでに決定済み
			return;
		}
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		int pid = Integer.parseInt(bean.getName().split("@")[0]);
		if(pid < 1000) {
			pid += 1000;
		}
		for(;pid < 65535;pid += 1000) {
			try {
				openJMXRemoteServer(pid);
				return;
			}
			catch (Exception e) {
			}
		}
		throw new Exception("jmx用のポート番号が決定できませんでした");
	}
}
