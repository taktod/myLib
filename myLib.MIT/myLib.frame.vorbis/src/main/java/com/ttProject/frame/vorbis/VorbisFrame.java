/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis;

import java.nio.ByteBuffer;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.vorbis.type.IdentificationHeaderFrame;

/**
 * vorbisのframe
 * @author taktod
 * vorbisのframeもspeexと同様
 * header部
 * コメント部
 * 情報部
 * データ部にわかれるっぽい。
 * 
 * で、データ部だけ、xuggleのframeとしてほしいところ。
 */
public abstract class VorbisFrame extends AudioFrame {
	/** データ参照用のIdentificationHeaderFrame */
	private IdentificationHeaderFrame identificationHeaderFrame = null;
	/**
	 * identificationHeaderFrame(情報を保持している)を設定
	 * @param headerFrame
	 */
	public void setIdentificationHeaderFrame(IdentificationHeaderFrame headerFrame) {
		this.identificationHeaderFrame = headerFrame;
		super.setBit(headerFrame.getBit());
		super.setChannel(headerFrame.getChannel());
		super.setSampleRate(headerFrame.getSampleRate());
		super.setSampleNum(headerFrame.getSampleNum());
	}
	/**
	 * identificationHeaderFrameを参照
	 * @return
	 */
	protected IdentificationHeaderFrame getHeaderFrame() {
		return identificationHeaderFrame;
	}
	/**
	 * codec用のprivateデータを応答します。
	 * identification + comment + setupの組み合わせのデータとなります。
	 * @return
	 */
	public ByteBuffer getCodecPrivate() throws Exception {
		return getPrivateData();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPrivateData() throws Exception {
		if(identificationHeaderFrame == null) {
			return null;
		}
		return identificationHeaderFrame.getPackBuffer();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VORBIS;
	}
}
