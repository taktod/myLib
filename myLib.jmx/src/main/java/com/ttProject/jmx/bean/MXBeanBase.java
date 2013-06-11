package com.ttProject.jmx.bean;

import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import com.ttProject.jmx.JMXFactory;

/**
 * MXBeanの実体ベース
 * @author taktod
 */
public abstract class MXBeanBase extends NotificationBroadcasterSupport implements IMXBeanBase {
	/**
	 * 自分の動作objectNameの保持
	 */
	private ObjectName objectName = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregister() {
		JMXFactory.unregisterMBean(objectName);
	}
	/**
	 * ObjectNameの設定
	 * @param objectName
	 */
	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}
}
