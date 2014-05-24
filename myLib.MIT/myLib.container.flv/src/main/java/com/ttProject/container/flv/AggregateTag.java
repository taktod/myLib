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
 * flvの集合タグ(rtmpででてくるデータ)
 * @author taktod
 */
public class AggregateTag extends FlvTag {
	/**
	 * コンストラクタ
	 */
	public AggregateTag() {
		super(new Bit8(0xFF));
	}
	/** 集合タグの内部リスト */
	private List<FlvTag> tagList = new ArrayList<FlvTag>();
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		throw new RuntimeException("未定義");
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void load(IReadChannel channel) throws Exception {
		throw new RuntimeException("未定義");
	}
	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	protected void requestUpdate() throws Exception {
		// これは定義してもいいかもしれない
		throw new RuntimeException("未定義");
	}
	/**
	 * 集合タグ追加
	 * @param tag
	 */
	public void add(FlvTag tag) {
		tagList.add(tag);
	}
	/**
	 * 保持タグリスト参照
	 * @return
	 */
	public List<FlvTag> getList() {
		return new ArrayList<FlvTag>(tagList);
	}
	/**
	 * 保持タグ数参照
	 * @return
	 */
	public int count() {
		return tagList.size();
	}
}
