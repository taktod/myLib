/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * vorbisのheaderBufferを分割して、ByteBufferのリストとして応答します。
 * @author taktod
 */
public class VorbisHeaderBufferSplitter {
	public List<ByteBuffer> doSplit(IReadChannel channel) throws Exception {
		List<ByteBuffer> result = new ArrayList<ByteBuffer>();
		int num = BufferUtil.safeRead(channel, 1).get();
		List<Integer> sizeList = new ArrayList<Integer>();
		for(int i = 0;i < num;i ++) {
			sizeList.add((int)(BufferUtil.safeRead(channel, 1).get()));
		}
		for(Integer size : sizeList) {
			ByteBuffer buffer = BufferUtil.safeRead(channel, size);
			result.add(buffer);
		}
		ByteBuffer buffer = BufferUtil.safeRead(channel, channel.size() - channel.position());
		result.add(buffer);
		return result;
	}
}
