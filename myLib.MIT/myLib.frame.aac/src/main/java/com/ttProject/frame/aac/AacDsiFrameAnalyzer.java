/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.aac;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;
import com.ttProject.nio.channels.IReadChannel;

/**
 * analyezr for aac based dsi.
 * ex:aac on flv and mp4
 * @author taktod
 */
public class AacDsiFrameAnalyzer extends AudioAnalyzer {
	/**
	 * constructor
	 */
	public AacDsiFrameAnalyzer() {
		super(new AacDsiFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateData(IReadChannel channel) throws Exception {
		// usually privateData setting is the trigger to remake selector.
		// however, for aac, we don't need. just set.
		DecoderSpecificInfo dsi = new DecoderSpecificInfo();
		dsi.minimumLoad(channel);
		((AacDsiFrameSelector)getSelector()).setDecoderSpecificInfo(dsi);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.AAC;
	}
}
