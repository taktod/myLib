/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;

/**
 * interface for basic data.
 * @author taktod
 */
public interface IData {
	/**
	 * data
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getData() throws Exception;
	/**
	 * size
	 * @return
	 */
	public int getSize();
	/**
	 * minimum for loading
	 * @throws Exception
	 */
	public void minimumLoad(IReadChannel channel) throws Exception;
	/**
	 * complete for loading
	 * @param channel
	 * @throws Exception
	 */
	public void load(IReadChannel channel) throws Exception;
}
