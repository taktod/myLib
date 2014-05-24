/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.stsd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

public abstract class AudioRecord extends DescriptionRecord {
	/** ロガー */
	private Logger logger = Logger.getLogger(AudioRecord.class);
	private Bit8[] reserved1 = new Bit8[6];
	private Bit16 dataReferenceIndex = new Bit16();
	private Bit32[] reserved2 = new Bit32[2];
	private Bit16 channelCount = new Bit16();
	private Bit16 sampleSize = new Bit16();
	private Bit16 predefined = new Bit16();
	private Bit16 reserved3 = new Bit16();
	private Bit32 sampleRate = new Bit32();
	private List<Mp4Atom> boxes = new ArrayList<Mp4Atom>();
	{
		for(int i = 0;i < 6;i ++) {
			reserved1[i] = new Bit8();
		}
		for(int i = 0;i < 2;i ++) {
			reserved2[i] = new Bit32();
		}
	}
	// boxes
	public AudioRecord(Bit32 size, Bit32 name) {
		super(size, name);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(reserved1);
		loader.load(dataReferenceIndex);
		loader.load(reserved2);
		loader.load(channelCount, sampleSize,predefined,
				reserved3, sampleRate);
		int targetSize = getSize() - 0x24;
		IContainer container = null;
		StsdAtomReader reader = new StsdAtomReader();
		while(targetSize > 0 && (container = reader.read(channel)) != null) {
			logger.info("みつけたコンテナ:" + container);
			boxes.add((Mp4Atom)container);
			targetSize -= container.getSize();
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
