/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.Container;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.EbmlValue;

/**
 * basic for mkvTag.
 * @author taktod
 */
public abstract class MkvTag extends Container {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvTag.class);
	private final EbmlValue id;
	private final EbmlValue size;
	private MkvTagReader reader = null;
	/**
	 * constructor
	 * @param id
	 * @param size
	 */
	public MkvTag(Type id, EbmlValue size) {
		this.id = new EbmlValue();
		this.id.setEbmlValue(Type.getValue(id));
		this.size = size;
		super.setSize((int)(size.getLong() + (this.id.getBitCount() + this.size.getBitCount()) / 8));
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setPosition(channel.position() - (id.getBitCount() + size.getBitCount()) / 8);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getPosition() + getSize());
		super.update();
	}
	/**
	 * hold the mkvTagReader
	 * @param reader
	 */
	public void setMkvTagReader(MkvTagReader reader) {
		this.reader = reader;
	}
	/**
	 * ref the mkvTagReader
	 * @return
	 */
	protected MkvTagReader getMkvTagReader() {
		return reader;
	}
	/**
	 * ref the headerBuffer(mkvTag + mkvSize)
	 * @return
	 */
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(id, size);
	}
	/**
	 * ref the mkvSize
	 * @return
	 */
	protected int getMkvSize() {
		return size.get();
	}
	protected EbmlValue getTagId() {
		return id;
	}
	protected EbmlValue getTagSize() {
		return size;
	}
	/**
	 * for dump.
	 * @param space indent space count.
	 * @return
	 */
	public String toString(String space) {
		StringBuilder data = new StringBuilder(space);
		data.append(getClass().getSimpleName());
		data.append(" size:").append(Integer.toHexString(getMkvSize()));
		return data.toString();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toString("");
	}
}
