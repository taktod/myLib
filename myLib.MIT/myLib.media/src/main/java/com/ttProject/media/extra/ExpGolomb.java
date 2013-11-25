package com.ttProject.media.extra;

import org.apache.log4j.Logger;

/**
 * h264のexpGolomb値を取得する動作
 * @see http://en.wikipedia.org/wiki/Exponential-Golomb_coding
 * @author taktod
 */
public abstract class ExpGolomb extends Bit {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(ExpGolomb.class);
	/** 保持データ */
	private int value;
	/** 先頭の0のカウント値 */
	private int zeroCount = 0;
	/** 先頭の1を見つけたときのフラグ */
	private boolean find1Flg = false;
	/** 保持ビット数 */
	private int bitCount;
	/**
	 * bitデータ参照
	 * @return
	 */
	protected int getValue() {
		return value;
	}
	/**
	 * コンストラクタ
	 */
	public ExpGolomb() {
		super(0);
	}
	/**
	 * bitを登録していく。
	 * @param bit 登録bit1
	 * @return false:登録がおわったとき true:まだ登録が必要なとき
	 */
	public boolean addBit1(Bit1 bit) {
//		logger.info("addBit " + bit.toString());
		if(!find1Flg) {
			// はじめの0をカウントアップする部分
			if(bit.get() == 0) {
				zeroCount ++;
			}
			else {
				// みつけた。
				find1Flg = true;
				bitCount = zeroCount * 2 + 1;
				// ここから先は実データ
				value = 1;
			}
		}
		else {
			value = (value << 1) | bit.get();
			zeroCount --;
		}
		return zeroCount != 0;
	}
	/**
	 * dump
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(" zeroCount:").append(zeroCount);
		data.append(" find1Flg:").append(find1Flg);
		data.append(" bitCount:").append(bitCount);
		return data.toString();
	}
}
