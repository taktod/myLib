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
 *
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
	 * サイズを設定する
	 */
	public void setSize() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// ここつくっておかないとだめ
		byte[] data = new byte[getTagSize().get()];
		BitConnector connector = new BitConnector();
		super.setData(
				BufferUtil.connect(
					connector.connect(getTagId(), getTagSize()),
					ByteBuffer.wrap(data)
				)
		);
	}
}
