/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.nio.ByteBuffer;

import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.BufferUtil;

/**
 * Voidタグ
 * @author taktod
 */
public class Void extends MkvTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Void(EbmlValue size) {
		super(Type.Void, size);
	}
	/**
	 * コンストラクタ
	 */
	public Void() {
		this(new EbmlValue());
	}
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Void(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * 位置を設定する
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
	/**
	 * tagのサイズを設定する
	 * @param size
	 */
	public void setTagSize(int size) {
		getTagSize().set(size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// ここつくっておかないとだめ
		byte[] empty = new byte[getTagSize().get()];
		BitConnector connector = new BitConnector();
		ByteBuffer data = BufferUtil.connect(
				connector.connect(getTagId(), getTagSize()),
				ByteBuffer.wrap(empty));
		setSize(data.remaining());
		super.setData(data);
	}
}
