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
	private long floatData = 0;
	/** 残っているbit数 */
	private int left = 0;
	/** エンディアンコントロール */
	private boolean littleEndianFlg = false;
	/**
	 * 動作エンディアンをlittleEndianに変更する
	 * @param flg
	 */
	public void setLittleEndianFlg(boolean flg) {
		littleEndianFlg = flg;
	}
	/**
	 * littleEndianとして動作しているか確認
	 * @return
	 */
	public boolean isLittleEndian() {
		return littleEndianFlg;
	}
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
		if(bit instanceof Ebml) {
			Ebml ebml = (Ebml) bit;
			Bit1 bit1 = null;
			do {
				bit1 = new Bit1();
				load(bit1);
			} while(ebml.addBit1(bit1)); // 1bitずつ読み込んでいって、実際のデータサイズをしる。
			load(ebml.getDataBit()); // 残りのデータbit数を読み込ませる
		}
		else if(bit instanceof ExpGolomb) {
			ExpGolomb golomb = (ExpGolomb) bit;
			Bit1 bit1 = null;
			do {
				bit1 = new Bit1();
				load(bit1);
			} while(golomb.addBit1(bit1));
		}
		else {
			if(littleEndianFlg) {
				while(left < bit.bitCount) {
					floatData = (floatData | (BufferUtil.safeRead(channel, 1).get() & 0xFFL) << left);
					left += 8;
				}
				int bitCount = bit.bitCount;
				if(bit instanceof BitN) {
					((BitN) bit).setLong(floatData & ((1L << bitCount) - 1));
				}
				else {
					bit.set((int)(floatData & ((1L << bitCount) - 1)));
				}
				if(bitCount == 64) {
					// 64bitのシフト動作はなにもしない動作になるみたいなので、32 x 2にしておく
					floatData >>>= 32;
					floatData >>>= 32;
				}
				else {
					floatData >>>= bitCount;
				}
				left -= bitCount;
			}
			else {
				// TODO BitNを分割して読み込む動作がなくなったことで64bit以上のBitNデータが読み込み不能になっている。(nellymoserの読み込みでこまるはず)
				// とりあえずbigEndianだけ対応しておく。
				if(bit instanceof BitN && bit.bitCount > 64) {
					for(Bit b : ((BitN)bit).bits) {
						load(b);
					}
				}
				else {
					while(left < bit.bitCount) {
						floatData = (floatData << 8 | (BufferUtil.safeRead(channel, 1).get() & 0xFFL));
						left += 8;
					}
					int bitCount = bit.bitCount;
					if(bit instanceof BitN) {
						((BitN) bit).setLong(floatData >>> (left - bitCount));
					}
					else {
						bit.set((int)(floatData >>> (left - bitCount)));
					}
					left -= bitCount;
				}
			}
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
