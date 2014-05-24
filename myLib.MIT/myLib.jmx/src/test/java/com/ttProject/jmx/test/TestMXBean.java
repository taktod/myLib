/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx.test;

import com.ttProject.jmx.bean.MXBeanBase;

/**
 * jmxBeanの実体
 * @author taktod
 */
public class TestMXBean extends MXBeanBase implements ITestMXBean {
	private boolean workFlg = true;
	@Override
	public String getName() {
		return "Test";
	}
	@Override
	public int getData() {
		return (int)(Math.random() * 100);
	}
	@Override
	public synchronized void stopProcess() {
		workFlg = false;
	}
	public synchronized boolean isWork() {
		return workFlg;
	}
	@Override
	public boolean getWorkFlg() {
		return workFlg;
	}
}
