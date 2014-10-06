/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.EbmlValue;

/**
 * mkvMasterTag
 * this tag have some tag inside.
 * @author taktod
 */
public abstract class MkvMasterTag extends MkvTag {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvMasterTag.class);
	/** holding tags. */
	private List<MkvTag> childTags = new ArrayList<MkvTag>();
	/**
	 * constructor
	 * @param tag
	 * @param size
	 */
	public MkvMasterTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int targetSize = getMkvSize();
		IContainer container = null;
		while(targetSize > 0 && (container = getMkvTagReader().read(channel)) != null) {
			targetSize -= container.getSize();
			if(container instanceof MkvTag) {
				childTags.add((MkvTag)container);
			}
		}
		super.load(channel);
	}
	/**
	 * add child tag
	 */
	public void addChild(MkvTag tag) {
		childTags.add(tag);
		super.update();
	}
	/**
	 * rev child tag.
	 * @param i
	 * @return
	 */
	public MkvTag removeChild(int i) {
		MkvTag removedTag = childTags.remove(i);
		super.update();
		return removedTag;
	}
	/**
	 * ref child tags.
	 * @return
	 */
	public List<MkvTag> getChildList() {
		return new ArrayList<MkvTag>(childTags);
	}
	/**
	 * set the size infinite(for live streaming)
	 * @param set true:infinite false:not-infinite.
	 */
	public void setInfinite(boolean set) {
		if(set) {
			getTagSize().setLong(0xFFFFFFFFFFFFFFL);
		}
		else {
			getTagSize().set(0);
		}
		super.update();
	}
	/**
	 * {@inheritDoc}
	 * note only reply ebml header and ebml size. child tags will report recursively.
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(getTagSize().getLong() == 0xFFFFFFFFFFFFFFL) {
			// reply only for ebml header and ebml size.(this is for segment only.)
			// TODO same as getHeaderBuffer()?
			BitConnector connector = new BitConnector();
			super.setData(connector.connect(getTagId(), getTagSize()));
			return;
		}
		int size = 0;
		for(MkvTag tag : childTags) {
			tag.getData();
			size += tag.getSize();
		}
		getTagSize().set(size);
		BitConnector connector = new BitConnector();
		ByteBuffer data = connector.connect(getTagId(), getTagSize());
		setSize(data.remaining() + size);
		super.setData(data);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append("*");
		return data.toString();
	}
}
