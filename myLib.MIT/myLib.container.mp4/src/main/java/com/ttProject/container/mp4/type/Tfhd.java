/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.container.mp4.table.SampleFlags;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * tfhdの定義
 * @author taktod
 */
public class Tfhd extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Tfhd.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	
	private Bit32 trackId = null; // このtrackIdがtkhdのtrackIdと一致するみたいです。
	private Bit64 baseDataOffset         = null; // 0x01
	private Bit32 sampleDescriptionIndex = null; // 0x02
	private Bit32 defaultSampleDuration  = null; // 0x08
	private Bit32 defaultSampleSize      = null; // 0x10
	private SampleFlags defaultSampleFlags = null; // 0x20

	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Tfhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Tfhd() {
		super(new Bit32(), Type.getTypeBit(Type.Tfhd));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(version, flags);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		trackId = new Bit32();
		if((flags.get() & 0x01) != 0x00) {
			baseDataOffset = new Bit64();
		}
		if((flags.get() & 0x02) != 0x00) {
			sampleDescriptionIndex = new Bit32();
		}
		if((flags.get() & 0x08) != 0x00) {
			defaultSampleDuration = new Bit32();
		}
		if((flags.get() & 0x10) != 0x00) {
			defaultSampleSize = new Bit32();
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(trackId, baseDataOffset, sampleDescriptionIndex, defaultSampleDuration, defaultSampleSize);
		if((flags.get() & 0x20) != 0x00) {
			// defaultSampleFlagsを読み込む
			throw new Exception("detect defaultSampleFlags. I need a sample.");
		}
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
