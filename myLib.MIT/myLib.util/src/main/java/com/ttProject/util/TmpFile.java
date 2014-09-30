/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.util;

import java.io.File;

/**
 * util for tmpFile.
 * @author taktod
 */
public class TmpFile extends File {
	private static final long serialVersionUID = -5496373580008414933L;
	/** expire */
	public final long expire = 3600;
	/**
	 * constructor
	 * @param path
	 */
	public TmpFile(String path) {
		// make on tmpdir.
		super(System.getProperty("java.io.tmpdir") + path);
		// make directories for path.
		getParentFile().mkdirs();
		// force delete on exit.
		deleteOnExit();
	}
}
