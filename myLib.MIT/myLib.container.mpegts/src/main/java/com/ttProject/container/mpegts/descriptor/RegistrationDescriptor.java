/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.descriptor;

import com.ttProject.container.mpegts.field.IDescriptorHolder;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

public class RegistrationDescriptor extends Descriptor {
	private String formatIdentifier; // 4byte
	private String additionalIdentificationInfo = "";
	/**
	 * constructor
	 * @param descriptorLength
	 */
	public RegistrationDescriptor(Bit8 descriptorLength, IDescriptorHolder holder) {
		super(new Bit8(DescriptorType.registration_descriptor.intValue()), descriptorLength, holder);
	}
	/**
	 * constructor
	 */
	public RegistrationDescriptor(IDescriptorHolder holder) {
		super(new Bit8(DescriptorType.registration_descriptor.intValue()), holder);
	}
	public String getFormatIdentifier() {
		return formatIdentifier;
	}
	public String getAdditionalIdentificationInfo() {
		return additionalIdentificationInfo;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int length = getDescriptorLength().get();
		// 4byte load
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
