/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.myLib.setup;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * テスト
 * @author taktod
 *
 */
public class PathTest {
	private Logger logger = Logger.getLogger(PathTest.class);
//	@Test
	public void test() {
		logger.info(getTargetFile("../a/b/c/d/test.flv"));
	}
	/**
	 * 
	 * @param path
	 * @param file
	 * @return
	 */
	public String getTargetFile(String file) {
		String[] data = file.split("/");
		File f = new File(".");
		f = new File(f.getAbsolutePath());
		f = f.getParentFile().getParentFile();
		for(String path : data) {
			f = new File(f.getAbsolutePath(), path);
		}
		f.getParentFile().mkdirs();
		return f.getAbsolutePath();
	}
}
