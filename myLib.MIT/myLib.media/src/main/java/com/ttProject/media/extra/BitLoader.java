package com.ttProject.media.extra;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * bitデータを読み込む動作
 * @author taktod
 */
public class BitLoader {
	/** 動作buffer */
//	private final CacheBuffer buffer;
	private final IReadChannel channel;
	/** 中途処理バッファ */
	private int floatData = 0;
	/** 残っているbit数 */
	private int left = 0;
	/**
	 * コンストラクタ
	 * @param channel
	 */
	public BitLoader(IReadChannel channel) throws Exception {
//		buffer = new CacheBuffer(channel);
		this.channel = channel;
	}
	/**
	 * bitデータを読み込みます。
	 * @param bit 一応8bitより大きなデータがきても大丈夫なはずですが、32bit超えるとoverflowします。
	 */
	public void load(Bit bit) throws Exception {
		if(bit instanceof ExpGolomb) {
			ExpGolomb golomb = (ExpGolomb) bit;
			Bit1 bit1 = null;
			do {
				bit1 = new Bit1();
				load(bit1);
			}while(golomb.addBit1(bit1));
		}
		else {
			while(left < bit.bitCount) {
//				floatData = (floatData << 8 | (buffer.get() & 0xFF));
				floatData = (floatData << 8 | (BufferUtil.safeRead(channel, 1).get() & 0xFF));
				left += 8;
			}
			int bitCount = bit.bitCount;
			bit.set(floatData >>> (left - bitCount));
			left -= bitCount;
		}
	}
	/**
	 * 大量のデータを一気に読み込みます。
	 * @param bits
	 * @throws Exception
	 */
	public void load(Bit... bits) throws Exception {
		for(Bit bit : bits) {
			load(bit);
		}
	}
}
