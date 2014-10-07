/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.container.mpegts.field.IDescriptorHolder;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * basic for descriptor
 * @author taktod
 */
public abstract class Descriptor {
	/** tag id */
	private final Bit8 descriptorTag;
	/** data size */
	private Bit8 descriptorLength = new Bit8();
	/** object for descriptor */
	private IDescriptorHolder holder = null;
	/**
	 * constructor
	 * @param tag
	 * @param length
	 */
	public Descriptor(Bit8 tag, Bit8 length, IDescriptorHolder holder) {
		this(tag, holder);
		descriptorLength = length;
	}
	/**
	 * constructor
	 * @param tag
	 * @param holder
	 */
	public Descriptor(Bit8 tag, IDescriptorHolder holder) {
		descriptorTag = tag;
		this.holder = holder;
	}
	/**
	 * ref tag id
	 * @return
	 */
	public Bit8 getDescriptorTag() {
		return descriptorTag;
	}
	/**
	 * ref size
	 * @return
	 */
	public Bit8 getDescriptorLength() {
		return descriptorLength;
	}
	/**
	 * set size
	 * @param length
	 */
	public void setDescriptorLength(Bit8 length) {
		descriptorLength = length;
	}
	/**
	 * ref the data size.
	 * @return
	 */
	public int getSize() {
		return descriptorLength.get() + 2; // tag + tag length byte + length
	}
	/**
	 * update size
	 */
	public void updateSize() {
		if(holder != null) {
			holder.updateSize();
		}
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(descriptorTag);
		list.add(descriptorLength);
		return list;
	}
	/**
	 * get the information of descriptor
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public static Descriptor getDescriptor(IReadChannel channel, IDescriptorHolder holder) throws Exception {
		Bit8 descriptorTag = new Bit8();
		Bit8 descriptorLength = new Bit8();
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(descriptorTag, descriptorLength);
		switch(DescriptorType.getType(descriptorTag.get())) {
		case registration_descriptor:
			RegistrationDescriptor registrationDescriptor = new RegistrationDescriptor(descriptorLength, holder);
			registrationDescriptor.load(channel);
			return registrationDescriptor;
		case ISO_639_language_descriptor:
			ISO639LanguageDescriptor iso639LanguageDescriptor = new ISO639LanguageDescriptor(descriptorLength, holder);
			iso639LanguageDescriptor.load(channel);
			return iso639LanguageDescriptor;
		case service_descriptor:
			ServiceDescriptor serviceDescriptor = new ServiceDescriptor(descriptorLength, holder);
			serviceDescriptor.load(channel);
			return serviceDescriptor;
		default: // unknown descriptor
			throw new Exception("unknown descriptor type is found. I need sample.");
		}
	}
	public abstract void load(IReadChannel channel) throws Exception;
}
