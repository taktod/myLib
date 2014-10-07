/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp3;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.mp3.type.Frame;

/**
 * mp3 writer
 * @author taktod
 */
public class Mp3UnitWriter implements IWriter {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Mp3UnitWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	/**
	 * constructor
	 * @param fileName
	 */
	public Mp3UnitWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public Mp3UnitWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public Mp3UnitWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		if(frame instanceof Frame) {
			outputChannel.write(frame.getData());
		}
	}
	@Override
	public void prepareHeader(CodecType ...codecs) {
	}
	@Override
	public void prepareTailer() {
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {
			}
			outputStream = null;
		}
	}
}
