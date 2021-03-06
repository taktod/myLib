/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.descriptor;

import com.ttProject.container.mpegts.field.IDescriptorHolder;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit8;

public class ISO639LanguageDescriptor extends Descriptor {
	private Bit24 ISO639LanguageCode;
	private Bit8 audioType;
	/**
	 * constructor
	 * @param descriptorLength
	 */
	public ISO639LanguageDescriptor(Bit8 descriptorLength, IDescriptorHolder holder) {
		super(new Bit8(DescriptorType.ISO_639_language_descriptor.intValue()), descriptorLength, holder);
	}
	/**
	 * constructor
	 */
	public ISO639LanguageDescriptor(IDescriptorHolder holder) {
		super(new Bit8(DescriptorType.ISO_639_language_descriptor.intValue()), holder);
	}
	public Bit24 getISO639LanguageCode() {
		return ISO639LanguageCode;
	}
	public Bit8 getAudioType() {
		return audioType;
	}
	/**
	 * {@inheritDoc}
	 * @param channel
	 * @throws Exception
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int length = getDescriptorLength().get();
		if(length == 4) {
			BitLoader loader = new BitLoader(channel);
			ISO639LanguageCode = new Bit24();
			audioType = new Bit8();
			loader.load(ISO639LanguageCode, audioType);
		}
		else {
			throw new Exception("unexpected length is found.");
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("    ");
		data.append("ISO639LanguageDescriptor:");
		data.append(" length:").append(Integer.toHexString(getDescriptorLength().get()));
		data.append(" ISO639LanguageCode:").append(Integer.toHexString(ISO639LanguageCode.get()));
		data.append(" audioType:").append(audioType);
		return data.toString();
	}
}
