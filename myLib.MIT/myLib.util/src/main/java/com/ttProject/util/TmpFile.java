/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.util;

import java.io.File;

/**
 * 一時ファイル作成用のプログラム
 * @author taktod
 */
public class TmpFile extends File {
	private static final long serialVersionUID = -5496373580008414933L;
	// 時間が経つと消す？
	public final long expire = 3600;
	public TmpFile(String path) {
		// 一時ファイルを作成します。
		super(System.getProperty("java.io.tmpdir") + path);
		getParentFile().mkdirs();
		deleteOnExit(); // プロセス完了時に消しておく。
	}
}
