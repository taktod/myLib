/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.BufferUtil;

/**
 * Binaryデータを保持するTagの動作
 * @author taktod
 */
public abstract class MkvBinaryTag extends MkvTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvBinaryTag.class);
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvBinaryTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, getRemainedSize());
		super.load(channel);
	}
	/**
	 * loadで読み込むべき残りデータ量を応答
	 * minimumLoadで継承先クラスがデータ量を変更している場合はここで調整する必要あり
	 * @return
	 */
	protected int getRemainedSize() {
		return getMkvSize();
	}
	/**
	 * データ参照
	 * @return
	 */
	public ByteBuffer getMkvData() {
		return buffer.duplicate();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		if(buffer == null) {
			data.append(" binary:").append("null");
		}
		else {
			data.append(" binary:").append(Integer.toHexString(buffer.remaining()));
		}
		return data.toString();
	}
}
