package com.ttProject.log4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 全threadのloggerにNDCを挿入します
 * @author taktod
 */
public class AllThreadNDCInjection {
	/** 登録したいデータ */
	protected static List<String> data = new ArrayList<String>();
	/** 登録したいデータ設定 */
	public static void setup(String ... list) {
		data.clear();
		for(String item : list) {
			data.add(item);
		}
	}
}
