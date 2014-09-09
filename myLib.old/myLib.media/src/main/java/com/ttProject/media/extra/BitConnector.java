/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.extra;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * bitデータのコネクト処理
 * @author taktod
 */
public class BitConnector {
	private int data = 0;
	private int left = 0;
	/**
	 * 接続します。
	 * @param bits
	 */
	public ByteBuffer connect(Bit... bits) {
		data = 0;
		left = 0;
		int size = 0;
		for(Bit bit : bits) {
			size += bit.bitCount;
		}
		ByteBuffer buffer = ByteBuffer.allocate((int)(Math.ceil(size / 8.0D)));
		for(Bit bit : bits) {
			data = (data << bit.bitCount) | bit.get();
			left += bit.bitCount;
			while(left > 8) {
				left -= 8;
				buffer.put((byte)((data >>> left) & 0xFF));
			}
		}
		buffer.put((byte)((data >>> (8 - left)) & 0xFF));
		buffer.flip();
		return buffer;
	}
	/**
	 * collectionFrameWorkの場合
	 * @param bits
	 * @return
	 */
	public ByteBuffer connect(List<Bit> bits) {
		return connect(bits.toArray(new Bit[]{}));
	}
}
