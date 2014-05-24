/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mp3.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ID3Tagは作成する必要あり。(とりあえずあとまわし)
 * 終端にあるので、無視しておく。
 * @author taktod
 */
public class TagFrame extends Mp3Frame {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
}
