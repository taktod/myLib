/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.util.HexUtil;

/**
 * string test
 * @author taktod
 *
 */
public class StringTest {
	/** logger */
	private Logger logger = Logger.getLogger(StringTest.class);
	@Test
	public void test() throws Exception {
		String a = "あいうえお";
		logger.info(a.length());
		logger.info(a.getBytes().length);
		logger.info(HexUtil.toHex(a.getBytes()));
		logger.info(HexUtil.toHex(a.getBytes("UTF-8")));
	}
}
