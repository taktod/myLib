/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.header.type;

import java.nio.ByteBuffer;

import com.ttProject.rtmp.header.HeaderType;
import com.ttProject.rtmp.header.RtmpHeader;

/**
 * Type3
 * @author taktod
 */
public class Type3 extends RtmpHeader {
	/**
	 * constructor
	 */
	public Type3() {
		super(HeaderType.Type3);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() {
		return getHeaderTypeChunkStreamIdBytes();
	}
}
