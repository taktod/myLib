/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.test;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.amf.Amf0Object;
import com.ttProject.container.flv.amf.Amf0Value;
import com.ttProject.util.HexUtil;

public class CommandTest {
	private Logger logger = Logger.getLogger(CommandTest.class);
//	@Test
	public void test() throws Exception {
		logger.info("test start");
		logger.info(HexUtil.toHex(Amf0Value.getValueBuffer("connect"), true));
		logger.info(HexUtil.toHex(Amf0Value.getValueBuffer(1), true));
		Amf0Object<String, Object> obj = new Amf0Object<String, Object>();
		obj.put("test", "hoge");
		obj.put("aiueo", 135);
		logger.info(HexUtil.toHex(Amf0Value.getValueBuffer(obj), true));
	}
}
