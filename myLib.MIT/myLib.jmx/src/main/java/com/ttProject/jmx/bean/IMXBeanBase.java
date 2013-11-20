package com.ttProject.jmx.bean;

import javax.management.MXBean;

/**
 * MXBeanのインターフェイスベース
 * @author taktod
 */
@MXBean
public interface IMXBeanBase {
	/**
	 * 自分自身を登録解除する命令
	 * (JMXそのものも１つの参照なので、なくさないとデータが永遠に残ってしまう。)
	 */
	public void unregister();
}
