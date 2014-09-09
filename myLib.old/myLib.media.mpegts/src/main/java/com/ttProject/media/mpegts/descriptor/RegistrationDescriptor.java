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

public class RegistrationDescriptor extends Descriptor {
	private String formatIdentifier; // 4バイト
	private String additionalIdentificationInfo = ""; // 任意サイズ(0でもいい)
	// legendってあるけど・・・
	/**
	 * コンストラクタ(解析系で利用する予定)
	 * @param descriptorLength
	 */
	public RegistrationDescriptor(Bit8 descriptorLength) {
		super(new Bit8(DescriptorType.registration_descriptor.intValue()), descriptorLength);
	}
	/**
	 * コンストラクタ(書き込み系で利用)
	 */
	public RegistrationDescriptor() {
		super(new Bit8(DescriptorType.registration_descriptor.intValue()));
	}
	public String getFormatIdentifier() {
		return formatIdentifier;
	}
	public String getAdditionalIdentificationInfo() {
		return additionalIdentificationInfo;
	}
	/**
	 * 解析動作
	 * @param channel
	 * @throws Exception
	 */
	public void analyze(IReadChannel channel) throws Exception {
		int length = getDescriptorLength().get();
		// 4バイト読み込む
		formatIdentifier = new String(BufferUtil.safeRead(channel, 4).array());
		length -= 4;
		if(length != 0) {
			additionalIdentificationInfo = new String(BufferUtil.safeRead(channel, length).array());
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("    ");
		data.append("registrationDescriptor:");
		data.append(" length:").append(Integer.toHexString(getDescriptorLength().get()));
		data.append(" formatIdentifier:").append(formatIdentifier);
		data.append(" additionalIdentificationInfo:").append(additionalIdentificationInfo);
		return data.toString();
	}
}
