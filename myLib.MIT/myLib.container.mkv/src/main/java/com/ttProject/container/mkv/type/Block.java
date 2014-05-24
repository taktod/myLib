/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.Lacing;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;

/**
 * Blockタグ
 * @author taktod
 */
public class Block extends MkvBlockTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Block.class);
	private Bit4 reserved1          = new Bit4();
	private Bit1 invisibleFrameFlag = new Bit1();
	private Bit2 lacing             = new Bit2();
	private Bit1 reserved2          = new Bit1();
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Block(EbmlValue size) {
		super(Type.Block, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(reserved1, invisibleFrameFlag, lacing, reserved2);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Lacing getLacingType() throws Exception {
		return Lacing.getType(lacing.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getRemainedSize() {
		return getMkvSize() - (getTrackId().getBitCount() + 24) / 8;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
