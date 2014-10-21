/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * speex frame
 * 
 * speexでは、headerの部分が欠如しているらしい。(ffmpegの出力より)
 * これは推測ですが、どうやらspeexのoggファイル化したときにでる、header部分が固定化されているために、削除状態になっている感じ。
 * aacのdeviceSpecificInfoが１つで固定なので、省略されている感じ。
 * よってframe数はaudioTagごとに固定されているみたいです。
 * その確認として、２つのaudioTagが合体しているaudioTagをつくって、再生したところ、はじめの音がこわれました。
 * 正解な気がします。
 * 
 * 以上とりあえず推測
 * 
 * speexは1つのframeあたり320samplesで動作している模様です。
 * speexを含むoggFileは
 * OggPage[headerFrame]
 * OggPage[commentFrame]
 * OggPage[frame,frame,frame,frame....]
 * OggPage[frame,frame,frame,frame....]
 * OggPage[frame,frame,frame,frame....]
 * という構成になっているみたい。
 * 
 * narrowBandの場合
 * ----
 * 1bit WidebandBit
 * 4bit modeId
 * その他データ
 * ----
 * 
 * wideBandの場合
 * ----
 * narrowBandのデータ
 * 1bit widebandBit
 * 3bit modeId
 * その他データ
 * ----
 * となっているらしい。
 * とりあえずflvのspeexだけ、最少単位に変更して扱っておきたいところ・・・
 * 
 * wideBand固定っぽい
 * @see http://www.speex.org/docs/manual/speex-manual.pdf
 * 
 * @author taktod
 */
public class Frame extends SpeexFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	/** frameBuffer */
	private ByteBuffer frameBuffer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.setSize(channel.size());
		frameBuffer = BufferUtil.safeRead(channel, channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameBuffer is null.");
		}
		super.setData(frameBuffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isComplete() {
		return true;
	}
}
