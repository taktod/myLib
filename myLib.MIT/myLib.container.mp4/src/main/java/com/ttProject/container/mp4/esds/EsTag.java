/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp4.esds;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit8;

public class EsTag extends Tag {
	/** ロガー */
	private Logger logger = Logger.getLogger(EsTag.class);
	private Bit16 esId   = new Bit16();
	private Bit8  flags  = new Bit8();
	private List<Tag> tagList = new ArrayList<Tag>();
	public EsTag(Bit8 tag) {
		super(tag);
	}
	public EsTag() {
		super(new Bit8(0x03));
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// 内容を読み込む
		int endPosition = channel.position() + getSize(); // このtagのおわりの位置取得
		BitLoader loader = new BitLoader(channel);
		Bit8 tag = new Bit8();
		loader.load(esId, flags);
		while(channel.position() < endPosition) {
			loader = new BitLoader(channel);
			loader.load(tag);
			// subTagの内容を読み込ませる
			logger.info(TagType.getType(tag));
			Tag sTag = null;
			switch(TagType.getType(tag)) {
			case DecoderConfig:
				sTag = new DecoderConfig(tag);
				break;
			case SlConfig: // slconfigがはいるのはここっぽい。
				sTag = new SlConfig(tag);
				break;
			default:
				throw new Exception("found invalid type for EsTag.:" + TagType.getType(tag));
			}
			sTag.minimumLoad(channel);
			logger.info(sTag);
			tagList.add(sTag);
		}
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
