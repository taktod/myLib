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
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.IntUtil;

/**
 * hdlrの定義
 * @author taktod
 * こっちはminimumの時点でvideoかどうかわかっておきたい
 * handlerType
 * vide VideoTrack
 * soun AudioTrack
 * data DataTrack
 * hint HintTrack
 * odsm ObjectDescriptorStream
 * crsm ClockReferenceStream
 * sdsm SceneDescriptionStream
 * ocsm ObjectContentInfoStream
 * ipsm IPMP Stream
 * mjsm MPEG-J Stream
 * mdir Apple Meta Data iTunes Reader
 * mp7b MPEG-7 binary XML
 * mp7t MPEG-7 XML
 * appl Apple specific
 * meta Timed Metadata track
 */
public class Hdlr extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Hdlr.class);
	private Bit8    version     = new Bit8();
	private Bit24   flags       = new Bit24();
	private Bit32   predefined  = new Bit32();
	private Bit32   handlerType = new Bit32();
	private Bit32[] reserved    = new Bit32[3];
	{
		for(int i = 0;i < 3;i ++) {
			reserved[i] = new Bit32();
		}
	}
	private String  name;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Hdlr(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Hdlr() {
		super(new Bit32(), Type.getTypeBit(Type.Hdlr));
	}
	/**
	 * minimumLoadで読み込むようにしておく。
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(version, flags, predefined, handlerType);
		loader.load(reserved);
		logger.info(IntUtil.makeHexString(handlerType.get()));
		name = new String(BufferUtil.safeRead(channel, getSize() - 32).array());
		logger.info(name);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
