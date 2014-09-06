/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * integerのlistの動作テスト
 * @author taktod
 */
public class ListTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(ListTest.class);
	@Test
	public void test() throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(5);
		list.add(2);
		logger.info(list);
		logger.info(list.remove(((Integer)1).intValue()));
		logger.info(list);
	}
}
