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
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * tfdtの定義
 * dtsを定義するためのものかな？
 * 差分のtimestampを記録しているみたい。
 * @see http://www.3gpp.org/ftp/Inbox/LSs_from_external_bodies/ISO_IEC_JTC1_SG29_WG11/29n12310.zip
 * @see 29n12310t2.doc
 * @author taktod
 */
public class Tfdt extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Tfdt.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Bit data = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Tfdt(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Tfdt() {
		super(new Bit32(), Type.getTypeBit(Type.Tfdt));
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
		switch(version.get()) {
		case 0:
			data = new Bit32();
			break;
		case 1:
			data = new Bit64();
			break;
		default:
			throw new Exception("version is corrupted.");
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(data);
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
