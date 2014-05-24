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
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * smhdの定義
 * @author taktod
 */
public class Smhd extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Smhd.class);
	private Bit8 version = new Bit8();
	private Bit24 flags = new Bit24();
	private Bit16 balance = null;
	private Bit16 reserved = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Smhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Smhd() {
		super(new Bit32(), Type.getTypeBit(Type.Smhd));
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
		balance = new Bit16();
		reserved = new Bit16();
		BitLoader loader = new BitLoader(channel);
		loader.load(balance, reserved);
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
