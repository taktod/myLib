package com.ttProject.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.ttProject.jmx.bean.IMXBeanBase;
import com.ttProject.jmx.bean.MXBeanBase;

/**
 * JMXの登録等の実施
 * @author taktod
 */
public class JMXFactory {
	private static Logger logger = Logger.getLogger(JMXFactory.class);
	/**
	 * 動作ドメイン(jmxのmxBeanの欄の一覧の名称になります。)
	 */
	private static String domain = "com.ttProject:type=";
	/**
	 * 動作対象beanServer
	 */
	private static MBeanServer beanServer;
	
	/**
	 * staticデータの初期化
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
	 * MBeanServerの登録
	 * @param beanServer
	 */
	public static void setMBeanServer(MBeanServer beanServer) {
		JMXFactory.beanServer = beanServer;
	}
	/**
	 * MBeanServerの参照
	 * @return
	 */
	public static MBeanServer getMBeanServer() {
		return JMXFactory.beanServer;
	}
	/**
	 * ドメイン名の設定
	 * @param domain
	 */
	public static void setDomain(String domain) {
		JMXFactory.domain = domain;
	}
	/**
	 * ドメイン名の参照
	 * @return
	 */
	public static String getDomain() {
		return JMXFactory.domain;
	}
	/**
	 * オブジェクトの登録
	 * @param type
	 * @param obj
	 * @return
	 */
	public static ObjectName registerMBean(String type, IMXBeanBase obj) {
		return registerMBean(type, obj, new String[]{});
	}
	/**
	 * オブジェクトの登録
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
	 * オブジェクトの登録
	 * @param obj
	 * @param objectName
	 * @return
	 */
	public static ObjectName registerMBean(IMXBeanBase obj, ObjectName objectName) {
		try {
			// 同名のデータがあった場合は強制的に再読み込みさせる。
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
	 * オブジェクトの削除
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
	 * オブジェクトの削除
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
