/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * aggregated flv tag(this tag is in rtmp message.)
 * @author taktod
 */
public class AggregateTag extends FlvTag {
	/**
	 * constructor
	 */
	public AggregateTag() {
		super(new Bit8(0xFF));
	}
	/** list of tags */
	private List<FlvTag> tagList = new ArrayList<FlvTag>();
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		throw new RuntimeException("unsupported");
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void load(IReadChannel channel) throws Exception {
		throw new RuntimeException("unsupported");
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	protected void requestUpdate() throws Exception {
		// これは定義してもいいかもしれない
		throw new RuntimeException("unsupported");
	}
	/**
	 * add new tag for aggregate
	 * @param tag
	 */
	public void add(FlvTag tag) {
		tagList.add(tag);
	}
	/**
	 * ref the tags
	 * @return
	 */
	public List<FlvTag> getList() {
		return new ArrayList<FlvTag>(tagList);
	}
	/**
	 * count of the tags.
	 * @return
	 */
	public int count() {
		return tagList.size();
	}
}
