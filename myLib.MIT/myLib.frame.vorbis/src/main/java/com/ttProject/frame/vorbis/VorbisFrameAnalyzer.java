/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * Vorbisのフレームを解析する
 * @author taktod
 */
public class VorbisFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public VorbisFrameAnalyzer() {
		super(new VorbisFrameSelector());
	}
	/**
	 * vorbisのprivateデータを設定する
	 * @param channel
	 */
	public void setPrivateData(IReadChannel channel) throws Exception {
		// ここでcodecPrivateのデータを先行して解析する必要あり。
		// 02 1E 56
		// サイズ指定要素２つ
		// １つ目は0x1E
		// ２つ目は0x56
		// 残りは３つ目の要素
		// となります。
		// なおxuggleで変換する場合はIStreamCoderにこのcodecPrivateと同じものを渡す必要があるみたいです。
		BitLoader loader = new BitLoader(channel);
		Bit8 count = new Bit8();
		Bit8 identificationHeaderSize = new Bit8();
		Bit8 commentHeaderSize = new Bit8();
		loader.load(count, identificationHeaderSize, commentHeaderSize);
		if(count.get() != 2) {
			throw new Exception("count数がvorbisに合致していません。");
		}
		analyze(new ByteReadChannel(BufferUtil.safeRead(channel, identificationHeaderSize.get())));
		analyze(new ByteReadChannel(BufferUtil.safeRead(channel, commentHeaderSize.get())));
		analyze(new ByteReadChannel(BufferUtil.safeRead(channel, channel.size() - channel.position())));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VORBIS;
	}
}
