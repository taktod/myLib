/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.aac;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;
import com.ttProject.nio.channels.IReadChannel;

/**
 * dsiベースのaacのframeを解析する動作
 * flvとかmp4とかで利用する。
 * @author taktod
 */
public class AacDsiFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public AacDsiFrameAnalyzer() {
		super(new AacDsiFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateData(IReadChannel channel) throws Exception {
		// ここでselectorを作り直してもいいけど、aacDsiFrameSelectorについては、作り直さなくても問題ない。
		// ここでdsiのデータを設定する形にしておきます。
		DecoderSpecificInfo dsi = new DecoderSpecificInfo();
		dsi.minimumLoad(channel);
		((AacDsiFrameSelector)getSelector()).setDecoderSpecificInfo(dsi);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.AAC;
	}
}
