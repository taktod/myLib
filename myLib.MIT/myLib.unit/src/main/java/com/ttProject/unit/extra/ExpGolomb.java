package com.ttProject.unit.extra;

import java.util.ArrayList;
import java.util.List;

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
	private int value = 0;
	/** 先頭の0のカウント値 */
	private int zeroCount = 0;
	/** 先頭の1を見つけたときのフラグ */
	private boolean find1Flg = false;
	/** 保持ビット数 */
	private int bitCountTmp;
	/**  */
	protected final List<Bit> bits = new ArrayList<Bit>();
	{
		bitCount = 1;
		bits.add(new Bit1(1));
	}
	
	/**
	 * bitデータ参照
	 * @return
	 */
	@Override
	public int get() {
		return value;
	}
	@Override
	public void set(int value) {
		this.value = value;
		bits.clear();
		int data = value;
		int i;
		for(i = 0;data != 0; data >>= 1, i ++) {
			bits.add(0, new Bit1(data & 0x01));
		}
		int zeroCount = i - 1;
		for(;zeroCount >= 8;zeroCount -= 8) {
			bits.add(0, new Bit8());
		}
		switch(zeroCount) {
		case 1:
			bits.add(0, new Bit1());
			break;
		case 2:
			bits.add(0, new Bit2());
			break;
		case 3:
			bits.add(0, new Bit3());
			break;
		case 4:
			bits.add(0, new Bit4());
			break;
		case 5:
			bits.add(0, new Bit5());
			break;
		case 6:
			bits.add(0, new Bit6());
			break;
		case 7:
			bits.add(0, new Bit7());
			break;
		default:
			break;
		}
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
				bitCountTmp = zeroCount * 2 + 1;
				// ここから先は実データ
				value = 1;
			}
		}
		else {
			value = (value << 1) | bit.get();
			zeroCount --;
		}
		boolean end = zeroCount != 0;
		if(end) {
			// bitCountについて、記録しておくべき
			set(value);
		}
		return end;
	}
	/**
	 * dump
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(" zeroCount:").append(zeroCount);
		data.append(" find1Flg:").append(find1Flg);
		data.append(" bitCount:").append(bitCountTmp);
		return data.toString();
	}
	public String dump() {
		StringBuilder data = new StringBuilder();
		data.append(bits);
		return data.toString();
	}
}
