/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.webm;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;

import com.ttProject.container.mkv.MkvTagWriter;
import com.ttProject.container.mkv.type.EBML;

/**
 * webmのtagの書き出し
 * @author taktod
 */
public class WebmTagWriter extends MkvTagWriter {
	/**
	 * コンストラクタ
	 * @param fileName
	 * @throws Exception
	 */
	public WebmTagWriter(String fileName) throws Exception {
		super(fileName);
	}
	/**
	 * コンストラクタ
	 * @param fileOutputStream
	 */
	public WebmTagWriter(FileOutputStream fileOutputStream) {
		super(fileOutputStream);
	}
	/**
	 * コンストラクタ
	 * @param outputChannel
	 */
	public WebmTagWriter(WritableByteChannel outputChannel) {
		super(outputChannel);
	}
	@Override
	protected void setupEbml() throws Exception {
		EBML ebml = new EBML();
		ebml.setup(1, 1, "webm", 2, 2);
		
		addContainer(ebml);
	}
}
