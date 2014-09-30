/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.log4j;

import java.util.ArrayList;
import java.util.List;

/**
 * inject NDC for all loggers
 * @author taktod
 */
public class AllThreadNDCInjection {
	/** data for register */
	protected static List<String> data = new ArrayList<String>();
	/** setup data. */
	public static void setup(String ... list) {
		data.clear();
		for(String item : list) {
			data.add(item);
		}
	}
}
