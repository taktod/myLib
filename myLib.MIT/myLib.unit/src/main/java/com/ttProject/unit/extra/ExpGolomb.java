package com.ttProject.unit.extra;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.unit.extra.bit.Bit8;

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
	/** 保持bitデータ */
	protected final List<Bit> bits = new ArrayList<Bit>();
	{
		// 初期化として0を表現しておきます。
		bitCount = 1;
		bits.add(new Bit1(1));
	}
	protected int getData() {
		return value;
	}
	protected void setData(int value) {
		this.value = value;
		bits.clear();
		int data = value;
		bitCount = 0;
		int i;
		for(i = 0;data != 0; data >>= 1, i ++) {
			bits.add(0, new Bit1(data & 0x01));
			bitCount ++;
		}
		int zeroCount = i - 1;
		for(;zeroCount >= 8;zeroCount -= 8) {
			bits.add(0, new Bit8());
			bitCount += 8;
		}
		bitCount += zeroCount;
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
		if(!find1Flg) {
			// はじめの0をカウントアップする部分
			if(bit.get() == 0) {
				zeroCount ++;
			}
			else {
				// みつけた。
				find1Flg = true;
//				bitCount = zeroCount * 2 + 1;
				// ここから先は実データ
				value = 1;
			}
		}
		else {
			value = (value << 1) | bit.get();
			zeroCount --;
		}
		boolean end = zeroCount == 0;
		if(end) {
			// bitCountについて、記録しておくべき
			setData(value);
		}
		return !end;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		for(Bit b : bits) {
			data.append(b.toString());
		}
		return data.toString();
	}
}
