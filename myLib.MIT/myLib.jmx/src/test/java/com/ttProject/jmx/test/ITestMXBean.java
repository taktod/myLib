/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.jmx.test;

import com.ttProject.jmx.bean.IMXBeanBase;

/**
 * インターフェイス
 * @author taktod
 */
public interface ITestMXBean extends IMXBeanBase {
	public String getName();
	public int getData();
	public void stopProcess();
	public boolean getWorkFlg();
}
