/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;

/**
 * writer for media file.
 * can write with container.
 * I'm interested in writing with frame more.
 * @author taktod
 */
public interface IWriter {
	/**
	 * add container
	 * @param container
	 */
	@Deprecated
	public void addContainer(IContainer container) throws Exception;
	/**
	 * add frame
	 * @param trackId
	 * @param frame
	 */
	public void addFrame(int trackId, IFrame frame) throws Exception;
	/**
	 * prepare header information for the media file.
	 */
	public void prepareHeader(CodecType ...codecs) throws Exception;
	/**
	 * update media file on the end of writing.
	 */
	public void prepareTailer() throws Exception;
}
