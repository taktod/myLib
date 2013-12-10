package com.ttProject.unit.extra;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.util.BufferUtil;

/**
 * bitデータを読み込む動作
 * @author taktod
 */
public class BitLoader {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(BitLoader.class);
	/** 動作buffer */
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
			} while(golomb.addBit1(bit1));
		}
		else if(bit instanceof BitN) {
			BitN bitn = (BitN)bit;
			for(Bit b : bitn.bits) {
				load(b);
			}
		}
		else {
			while(left < bit.bitCount) {
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
	/**
	 * 1byteに満たない端数を応答します
	 * @return
	 */
	public Bit getExtraBit() throws Exception {
		Bit bit = null;
		switch(left) {
		case 1:
			bit = new Bit1();
			break;
		case 2:
			bit = new Bit2();
			break;
		case 3:
			bit = new Bit3();
			break;
		case 4:
			bit = new Bit4();
			break;
		case 5:
			bit = new Bit5();
			break;
		case 6:
			bit = new Bit6();
			break;
		case 7:
			bit = new Bit7();
			break;
		default:
			return null;
		}
		load(bit);
		return bit;
	}
}
