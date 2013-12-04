package com.ttProject.unit.extra;

import java.nio.ByteBuffer;

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
	protected final int bitCount;
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
	@Deprecated
	public static void bitLoader(IReadChannel channel, Bit... bits) throws Exception {
		// 読み込むべきサイズをしっておく。
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(bits);
	}
	/**
	 * bitデータを結合して１つのデータに復元する。
	 * @param bits
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static ByteBuffer bitConnector(Bit... bits) throws Exception {
		BitConnector bitConnector = new BitConnector();
		return bitConnector.connect(bits);
	}
}
