/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;

/**
 * interface of frameAnalyzer
 * @author taktod
 */
public interface IAnalyzer {
	/**
	 * analyze from IReadChannel
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IFrame analyze(IReadChannel channel) throws Exception;
	/**
	 * ref the frame on progress.
	 * @return
	 * @throws Exception
	 */
	public IFrame getRemainFrame() throws Exception;
	/**
	 * ref the target codecType
	 * @return
	 */
	public CodecType getCodecType();
	/**
	 * set the privateData for targetCodec.
	 * @param channel
	 * @throws Exception
	 */
	public void setPrivateData(IReadChannel channel) throws Exception;
}
