/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx.bean;

import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import com.ttProject.jmx.JMXFactory;

/**
 * base of MXBean.
 * @author taktod
 */
public abstract class MXBeanBase extends NotificationBroadcasterSupport implements IMXBeanBase {
	/** object name */
	private ObjectName objectName = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregister() {
		JMXFactory.unregisterMBean(objectName);
	}
	/**
	 * object name
	 * @param objectName
	 */
	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}
}
