/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Audioタグ
 * @author taktod
 */
public class Audio extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Audio(EbmlValue size) {
		super(Type.Audio, size);
	}
	/**
	 * コンストラクタ
	 */
	public Audio() {
		this(new EbmlValue());
	}
	/**
	 * 内容データをセットアップする動作
	 * @param frame
	 * @throws Exception
	 */
	public void setup(IAudioFrame frame) throws Exception {
		Channels channels = new Channels();
		channels.setValue(frame.getChannel());
		addChild(channels);
		SamplingFrequency samplingFrequency = new SamplingFrequency();
		samplingFrequency.setValue(frame.getSampleRate());
		addChild(samplingFrequency);
		BitDepth bitDepth = new BitDepth();
		bitDepth.setValue(frame.getBit());
		addChild(bitDepth);
	}
}
