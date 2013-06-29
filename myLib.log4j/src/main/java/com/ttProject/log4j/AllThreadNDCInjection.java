package com.ttProject.log4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 全threadのloggerにNDCを挿入します
 * @author taktod
 */
public class AllThreadNDCInjection {
	protected static List<String> data = new ArrayList<String>();
	public static void setup(String ... list) {
		for(String item : list) {
			data.add(item);
		}
	}
}
