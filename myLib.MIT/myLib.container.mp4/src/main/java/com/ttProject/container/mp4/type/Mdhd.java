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
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * mdhdの定義
 * @author taktod
 */
public class Mdhd extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Mdhd.class);
	private Bit8   version = new Bit8();
	private Bit24  flags   = new Bit24();
	private Bit    creationTime     = null;
	private Bit    modificationTime = null;
	private Bit32  timeScale        = null;
	private Bit    duration         = null;
	private Bit1   pad              = null;
	private Bit5[] language         = new Bit5[3];
	private Bit16  reserved         = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Mdhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Mdhd() {
		super(new Bit32(), Type.getTypeBit(Type.Mdhd));
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
		if(version.get() == 0) {
			creationTime = new Bit32();
			modificationTime = new Bit32();
			duration = new Bit32();
		}
		else if(version.get() == 1) {
			creationTime = new Bit64();
			modificationTime = new Bit64();
			duration = new Bit64();
		}
		else {
			throw new Exception("version is corrupted.");
		}
		timeScale = new Bit32();
		pad = new Bit1();
		language[0] = new Bit5();
		language[1] = new Bit5();
		language[2] = new Bit5();
		reserved = new Bit16();
		BitLoader loader = new BitLoader(channel);
		loader.load(creationTime, modificationTime, timeScale, duration, pad);
		loader.load(language);
		loader.load(reserved);
		// 言語コードの取得方法メモ
//		logger.info(new String(new byte[]{(byte)(0x60 + language[0].get()), (byte)(0x60+language[1].get()), (byte)(0x60+language[2].get())}));
//		logger.info(channel.position());
		super.load(channel);
//		logger.info(channel.position());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
