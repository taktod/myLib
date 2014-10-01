/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.nio.ByteBuffer;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.container.mkv.type.ContentCompAlgo.Algo;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ContentEncodingタグ
 * TODO こいつにデータを持たせておいてどういう圧縮がかかっているか参照できるようにしないと、SimpleBlockが動作できない。
 * @author taktod
 */
public class ContentCompression extends MkvMasterTag {
	private ContentCompAlgo     algo = null;
	private ContentCompSettings settings = null;
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentCompression(EbmlValue size) {
		super(Type.ContentEncoding, size);
	}
	/**
	 * コンストラクタ
	 */
	public ContentCompression() {
		this(new EbmlValue());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.load(channel);
		// このタイミングでデータができているはずなので、algoとsettingsをいれておく
		for(MkvTag tag : getChildList()) {
			if(tag instanceof ContentCompAlgo) {
				algo = (ContentCompAlgo)tag;
			}
			else if(tag instanceof ContentCompSettings) {
				settings = (ContentCompSettings)tag;
			}
		}
		if(algo == null) {
			throw new Exception("algo setting is required, but not found.");
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChild(MkvTag tag) {
		if(tag instanceof ContentCompAlgo) {
			algo = (ContentCompAlgo)tag;
		}
		if(tag instanceof ContentCompSettings) {
			settings = (ContentCompSettings)tag;
		}
		super.addChild(tag);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MkvTag removeChild(int i) {
		MkvTag removedTag = super.removeChild(i);
		if(removedTag.hashCode() == algo.hashCode()) {
			algo = null;
		}
		if(removedTag.hashCode() == settings.hashCode()) {
			settings = null;
		}
		return removedTag;
	}
	public Algo getAlgoType() throws Exception {
		if(algo == null) {
			throw new Exception("algo is undefined.");
		}
		return algo.getType();
	}
	public ByteBuffer getSettingData() throws Exception {
		if(getAlgoType() != Algo.HeaderStripping) {
			throw new Exception("algoType is unexpected.:" + getAlgoType());
		}
		if(settings == null) {
			throw new Exception("settings is undefined.");
		}
		return settings.getMkvData();
	}
}
