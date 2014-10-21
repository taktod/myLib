/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.CodecType;

/**
 * test
 * @author taktod
 */
public class CodecTypeListTest {
	private Logger logger = Logger.getLogger(CodecTypeListTest.class);
	@Test
	public void test() {
		logger.info("start test");
		List<CodecType> typeList = new ArrayList<CodecType>();
		typeList.add(CodecType.AAC);
		typeList.add(CodecType.SPEEX);
		typeList.add(CodecType.AAC);
		typeList.add(CodecType.VORBIS);
		logger.info(typeList);
		logger.info(typeList.remove(CodecType.AAC));
		logger.info(typeList);
		logger.info(typeList.remove(CodecType.AAC));
		logger.info(typeList);
		logger.info(typeList.remove(CodecType.AAC));
		logger.info(typeList);
	}
}
