/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;

/**
 * メディアデータを扱うマネージャー
 * @author taktod
 */
public abstract class Manager<T> {
	private ByteBuffer buffer = null;
	/**
	 * 追加bufferを足して、データを応答します。
	 * 応答データは読み込みモードとして返ってきます。
	 * @param data 読み込みモードの適当なデータ
	 * @return 読み込みモードの合成後のデータ(データがnullのこともあります。)
	 */
	protected ByteBuffer appendBuffer(ByteBuffer data) {
		if(data == null) {
			return buffer;
		}
		if(buffer != null) {
			int length = buffer.remaining() + data.remaining();
			ByteBuffer newBuffer = ByteBuffer.allocate(length);
			newBuffer.put(buffer);
			buffer = newBuffer;
		}
		else {
			int length = data.remaining();
			buffer = ByteBuffer.allocate(length);
		}
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	// データをいれると、取得可能になったデータがでてくる。
	public abstract List<T> getUnits(ByteBuffer data) throws Exception;
	// IReadChannelをいれると、１つ分のデータが取得できる。
	public abstract T getUnit(IReadChannel source) throws Exception;
}
