/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.CodecType;
import com.ttProject.container.riff.RiffUnit;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.BufferUtil;

/**
 * fmt
 * フォーマット情報
 * @author taktod
 */
public class Fmt extends RiffUnit {
	/** ロガー */
	private Logger logger = Logger.getLogger(Fmt.class);
	private Bit16 pcmType       = new Bit16();
	private Bit16 channels      = new Bit16();
	private Bit32 sampleRate    = new Bit32();
	private Bit32 dataSpeed     = new Bit32(); // データの転送速度らしい byte / sec
	private Bit16 blockSize     = new Bit16();
	private Bit16 bitNum        = new Bit16();
	private Bit16 extraInfoSize = new Bit16();
	@SuppressWarnings("unused")
	private ByteBuffer extraInfo = null;
	/** フレーム解析用のanalyzer */
	private IAnalyzer frameAnalyzer = null;
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(pcmType, channels, sampleRate, dataSpeed, blockSize, bitNum, extraInfoSize);
		logger.info(CodecType.getCodec(pcmType.get()));
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		extraInfo = BufferUtil.safeRead(channel, extraInfoSize.get());
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * コーデックタイプを参照する
	 * @return
	 */
	public CodecType getCodecType() {
		return CodecType.getCodec(pcmType.get());
	}
	/**
	 * フレームの解析プログラムを参照します。
	 * @return
	 */
	public IAnalyzer getFrameAnalyzer() {
		if(frameAnalyzer != null) {
			return frameAnalyzer;
		}
		switch(getCodecType()) {
		case IMA_ADPCM:
			frameAnalyzer = new AdpcmImaWavFrameAnalyzer();
			AudioSelector selector = ((AudioAnalyzer)frameAnalyzer).getSelector();
			// bit数は参考値とおもってOKだと思う。
//			selector.setBit(bitNum.get()); // 4bitになる(adpcmの１サンプルあたりの設定が4bitなため モノラルでも同じ)
			selector.setBit(16);
			selector.setChannel(channels.get());
			selector.setSampleRate(sampleRate.get());
			break;
		default:
			throw new RuntimeException("不明なコーデックでした。");
		}
		return frameAnalyzer;
	}
	public int getBlockSize() {
		return blockSize.get();
	}
}
