/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.unit;

import org.jboss.netty.buffer.ChannelBuffers;

import com.flazr.io.flv.FlvAtom;
import com.ttProject.container.flv.FlvTag;

/**
 * flvTagからflazrのflvAtomを取得する動作
 * @author taktod
 */
public class TagManager {
	/**
	 * FlvTagからflazrのflvAtomを取り出します。
	 * @param tag
	 * @return
	 * @throws Exception
	 */
	public FlvAtom getAtom(FlvTag tag) throws Exception {
		return new FlvAtom(ChannelBuffers.copiedBuffer(tag.getData()));
	}
}
