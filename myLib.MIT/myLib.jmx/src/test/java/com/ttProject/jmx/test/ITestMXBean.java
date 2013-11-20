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
