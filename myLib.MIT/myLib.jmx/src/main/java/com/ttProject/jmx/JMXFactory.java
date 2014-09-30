/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.ttProject.jmx.bean.IMXBeanBase;
import com.ttProject.jmx.bean.MXBeanBase;

/**
 * control jmx task.
 * @author taktod
 */
public class JMXFactory {
	private static Logger logger = Logger.getLogger(JMXFactory.class);
	/** work domain */
	private static String domain = "com.ttProject:type=";
	/** bean server */
	private static MBeanServer beanServer;
	
	/**
	 * static initialize
	 */
	static {
		try {
			setMBeanServer(MBeanServerFactory.findMBeanServer(null).get(0));
		}
		catch (Exception e) {
			setMBeanServer(ManagementFactory.getPlatformMBeanServer());
		}
	}
	/**
	 * register MBeanServer
	 * @param beanServer
	 */
	public static void setMBeanServer(MBeanServer beanServer) {
		JMXFactory.beanServer = beanServer;
	}
	/**
	 * MBeanServer
	 * @return
	 */
	public static MBeanServer getMBeanServer() {
		return JMXFactory.beanServer;
	}
	/**
	 * register domain name
	 * @param domain
	 */
	public static void setDomain(String domain) {
		JMXFactory.domain = domain;
	}
	/**
	 * domain name
	 * @return
	 */
	public static String getDomain() {
		return JMXFactory.domain;
	}
	/**
	 * register jmx object
	 * @param type
	 * @param obj
	 * @return
	 */
	public static ObjectName registerMBean(String type, IMXBeanBase obj) {
		return registerMBean(type, obj, new String[]{});
	}
	/**
	 * register jmx object
	 * @param type
	 * @param obj
	 * @param paths
	 * @return
	 */
	public static ObjectName registerMBean(String type, IMXBeanBase obj, String[] paths) {
		try {
			StringBuilder objectNameStr = new StringBuilder(domain);
			objectNameStr.append(type);
			int i = 0;
			for(String path : paths) {
				objectNameStr.append(",");
				objectNameStr.append(i);
				objectNameStr.append("=");
				objectNameStr.append(path);
				i ++;
			}
			ObjectName objectName = new ObjectName(objectNameStr.toString());
			return registerMBean(obj, objectName);
		}
		catch (Exception e) {
			logger.error("Could not register the " + obj.getClass().getName(), e);
			return null;
		}
	}
	/**
	 * register jmx object
	 * @param obj
	 * @param objectName
	 * @return
	 */
	public static ObjectName registerMBean(IMXBeanBase obj, ObjectName objectName) {
		try {
			// re-register for same name.
			if(beanServer.isRegistered(objectName)) {
				beanServer.unregisterMBean(objectName);
			}
			beanServer.registerMBean(obj, objectName);
			if(obj instanceof MXBeanBase) {
				((MXBeanBase)obj).setObjectName(objectName);
			}
			return objectName;
		}
		catch (Exception e) {
			logger.error("Could not register the MXBean", e);
			return null;
		}
	}
	/**
	 * unregister jmx object
	 * @param type
	 * @param paths
	 */
	public static void unregisterMBean(String type, String[] paths) {
		try {
			StringBuilder objectNameStr = new StringBuilder(domain);
			int i = 0;
			for(String path : paths) {
				objectNameStr.append(",");
				objectNameStr.append(i);
				objectNameStr.append("=");
				objectNameStr.append(path);
				i ++;
			}
			ObjectName objectName = new ObjectName(objectNameStr.toString());
			unregisterMBean(objectName);
		}
		catch (Exception e) {
			logger.error("Could not unregister the MXBean", e);
		}
	}
	/**
	 * unregister jmx object
	 * @param objectName
	 */
	public static void unregisterMBean(ObjectName objectName) {
		try {
			if(beanServer.isRegistered(objectName)) {
				beanServer.unregisterMBean(objectName);
			}
		}
		catch (Exception e) {
			logger.error("Could not unregister the MXBean", e);
		}
	}
}
