/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit;

import java.nio.ByteBuffer;

/**
 * base for data
 * @author taktod
 */
public abstract class Data implements IData {
	/** size */
	private int size = 0;
	/** holding data */
	private ByteBuffer data = null;
	/** flg for update */
	private boolean update = false;
	/**
	 * if something is changed, check the flag.
	 */
	protected final void update() {
		update = true;
	}
	/**
	 * if update is flaged, ask class to update data.
	 * @throws Exception
	 */
	protected abstract void requestUpdate() throws Exception;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return size;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		if(update) {
			requestUpdate();
		}
		return data.duplicate();
	}
	/**
	 * size
	 * @param size
	 */
	protected void setSize(int size) {
		this.size = size;
	}
	/**
	 * data
	 * @param data
	 */
	protected void setData(ByteBuffer data) {
		this.data = data;
		update = false;
	}
}
