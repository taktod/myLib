/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.adts;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.aac.type.Frame;

/**
 * adts unit writer.
 * @author taktod
 */
public class AdtsUnitWriter implements IWriter {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AdtsUnitWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	/**
	 * constructor
	 * @param fileName
	 */
	public AdtsUnitWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public AdtsUnitWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public AdtsUnitWriter(WritableByteChannel outputChannel) {
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
	public void prepareHeader(CodecType... codecs) throws Exception {
	}

	@Override
	public void prepareTailer() throws Exception {
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
