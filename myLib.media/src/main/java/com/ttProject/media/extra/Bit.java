package com.ttProject.media.extra;

import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BitUtil;

/**
 * bit型の基本クラス
 * @author taktod
 *
 */
public abstract class Bit {
	/** 保持データ */
	private byte value;
	private final int bitCount;
	public Bit(int count) {
		bitCount = count;
	}
	/**
	 * 内部データ設定
	 * @param value
	 */
	public void set(int value) {
		this.value = (byte)value;
	}
	/**
	 * 内部データ参照
	 * @return
	 */
	public int get() {
		return value & 0xFF;
	}
	/**
	 * データDump
	 */
	@Override
	public String toString() {
		return toString(bitCount);
	}
	/**
	 * データDump桁を合わせる
	 * @param bitsCount
	 * @return
	 */
	protected String toString(int bitsCount) {
		return BitUtil.toBit(value, bitsCount);
	}
	/**
	 * IReadChannelから指定Bitデータを読み込んで取り出す
	 * @param channel
	 * @param bits
	 * @return
	 */
	public static void bitLoader(IReadChannel channel, Bit... bits) throws Exception {
		// 読み込むべきサイズをしっておく。
		int size = 0;
		for(Bit bit : bits) {
			size += bit.bitCount;
		}
		CacheBuffer buffer = new CacheBuffer(channel, (int)Math.ceil(size / 8.0));
		// channelのデータを読みこむ
		int floatData = 0;
		int left = 0; // 読み込み待ちのbit数
		for(Bit bit : bits) {
			// bitデータを読み込む
			while(left < bit.bitCount) {
				floatData = (floatData << 8) | (buffer.get() & 0xFF);
				left += 8;
			}
			// 必要なデータ量読み込む
			int bitCount = bit.bitCount;
			bit.set(floatData >>> (left - bitCount));
			left -= bitCount;
		}
	}
}
