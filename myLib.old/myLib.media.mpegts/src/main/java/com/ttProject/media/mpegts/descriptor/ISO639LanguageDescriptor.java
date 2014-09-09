/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mpegts.descriptor;

import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

public class ISO639LanguageDescriptor extends Descriptor {
	private int ISO639LanguageCode; // 3バイト
	private Bit8 audioType; // 1バイト
	// legendってあるけど・・・
	/**
	 * コンストラクタ(解析用)
	 * @param descriptorLength
	 */
	public ISO639LanguageDescriptor(Bit8 descriptorLength) {
		super(new Bit8(DescriptorType.ISO_639_language_descriptor.intValue()), descriptorLength);
	}
	/**
	 * コンストラクタ(書き込み用)
	 */
	public ISO639LanguageDescriptor() {
		super(new Bit8(DescriptorType.ISO_639_language_descriptor.intValue()));
	}
	public int getISO639LanguageCode() {
		return ISO639LanguageCode;
	}
	public Bit8 getAudioType() {
		return audioType;
	}
	/**
	 * 解析動作
	 * @param channel
	 * @throws Exception
	 */
	public void analyze(IReadChannel channel) throws Exception {
		int length = getDescriptorLength().get();
		if(length == 4) {
			int data = BufferUtil.safeRead(channel, 4).getInt();
			ISO639LanguageCode = (data >>> 8);
			audioType = new Bit8(data & 0xFF);
		}
		else {
			throw new Exception("意図しないデータサイズを受け取りました");
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("    ");
		data.append("ISO639LanguageDescriptor:");
		data.append(" length:").append(Integer.toHexString(getDescriptorLength().get()));
		data.append(" ISO639LanguageCode:").append(Integer.toHexString(ISO639LanguageCode));
		data.append(" audioType:").append(audioType);
		return data.toString();
	}
}
