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
 * make jmx remote server
 * @author taktod
 */
public class JMXServerFactory {
	/** port number */
	private static int port = -1;
	/**
	 * ref for port.
	 * @return
	 */
	public static int getPort() {
		return port;
	}
	/**
	 * open jmx remote server(with port number)
	 * @param port target port number.
	 */
	public static void openJMXRemoteServer(int port) throws Exception {
		JMXServerFactory.port = port;
		LocateRegistry.createRegistry(port);
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
		JMXConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url, null, JMXFactory.getMBeanServer());
		server.start();
	}
	/**
	 * make remote server
	 * port number will be "pid + 1000 * x"
	 * @throws Exception
	 */
	public static void openJMXRemoteServer() throws Exception {
		if(JMXServerFactory.port != -1) {
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
		throw new Exception("cannot start jmx remote server.");
	}
}
