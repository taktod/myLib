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
 * make flvAtom from myLib.container.flv
 * @author taktod
 */
public class TagManager {
	/**
	 * get flvAtom from FlvTag.
	 * @param tag
	 * @return
	 * @throws Exception
	 */
	public FlvAtom getAtom(FlvTag tag) throws Exception {
		return new FlvAtom(ChannelBuffers.copiedBuffer(tag.getData()));
	}
}
