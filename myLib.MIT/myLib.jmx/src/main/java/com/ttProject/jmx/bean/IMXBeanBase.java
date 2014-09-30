/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx.bean;

import javax.management.MXBean;

/**
 * interface of MXBean base
 * @author taktod
 */
@MXBean
public interface IMXBeanBase {
	/**
	 * unregister myself.
	 */
	public void unregister();
}
