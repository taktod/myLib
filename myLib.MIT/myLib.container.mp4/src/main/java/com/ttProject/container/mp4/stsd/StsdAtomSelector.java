/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.stsd;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.stsd.record.Avcc;
import com.ttProject.container.mp4.stsd.record.Esds;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.IntUtil;

/**
 * stsdが保持しているatomの内部データ解析
 * @author taktod
 */
public class StsdAtomSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(StsdAtomSelector.class);
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		Bit32 size = new Bit32();
		Bit32 name = new Bit32();
		BitLoader loader = new BitLoader(channel);
		loader.load(size, name);
		logger.info(IntUtil.makeHexString(name.get()));
		String nameString = IntUtil.makeHexString(name.get());
		Mp4Atom atom = null;
		// .mp3もつくっておく必要あるかも？
		if("mp4a".equals(nameString)) {
			atom = new Mp4a(size, name);
		}
		else if("avc1".equals(nameString)) {
			atom = new Avc1(size, name);
		}
		else if("avcC".equals(nameString)) {
			atom = new Avcc(size, name);
		}
		else if("esds".equals(nameString)) {
			atom = new Esds(size, name);
		}
		else {
			throw new Exception("解析できない。mp4Atomでした。:" + nameString);
		}
		atom.minimumLoad(channel);
		return atom;
	}
}
