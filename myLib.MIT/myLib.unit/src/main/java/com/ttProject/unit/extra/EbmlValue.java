package com.ttProject.unit.extra;

import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit14;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit21;
import com.ttProject.unit.extra.bit.Bit28;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit35;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit42;
import com.ttProject.unit.extra.bit.Bit49;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit56;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.unit.extra.bit.Bit8;

public class EbmlValue extends Bit {
	private byte zeroCount = 0;
	private Bit numBit  = null;
	private Bit dataBit = null;
	/**
	 * コンストラクタ
	 * @param count
	 */
	public EbmlValue() {
		super(0);
	}
	@Override
	public int getBitCount() {
		return numBit.getBitCount() + dataBit.getBitCount();
	}
	@Override
	public int get() {
		long data = getLong();
		if(data > 0xFFFFFFFFL) {
			throw new RuntimeException("getLongを利用してデータを取得してください。");
		}
		return (int)data;
	}
	/**
	 * 値を参照する
	 * @return
	 */
	public long getLong() {
		if(dataBit instanceof BitN) {
			return ((BitN) dataBit).getLong();
		}
		else {
			return dataBit.get();
		}
	}
	/**
	 * ebmlの生データを参照する
	 * @return
	 */
	public long getEbmlValue() {
		return new BitN(numBit, dataBit).getLong();
	}
	@Override
	public void set(int value) {
		if(value >>> 7 == 0) {
			numBit = new Bit1(1);
			dataBit = new Bit7(value);
		}
		else if(value >>> 14 == 0) {
			numBit = new Bit2(1);
			dataBit = new Bit14(value);
		}
		else if(value >>> 21 == 0) {
			numBit = new Bit3(1);
			dataBit = new Bit21(value);
		}
		else if(value >>> 28 == 0) {
			numBit = new Bit4(1);
			dataBit = new Bit28(value);
		}
		else {
			numBit = new Bit5(1);
			dataBit = new Bit35(value);
		}
	}
	public void setLong(long value) {
		if(value >>> 7 == 0) {
			numBit = new Bit1(1);
			dataBit = new Bit7((int)value);
		}
		else if(value >>> 14 == 0) {
			numBit = new Bit2(1);
			dataBit = new Bit14((int)value);
		}
		else if(value >>> 21 == 0) {
			numBit = new Bit3(1);
			dataBit = new Bit21((int)value);
		}
		else if(value >>> 28 == 0) {
			numBit = new Bit4(1);
			dataBit = new Bit28((int)value);
		}
		else if(value >>> 35 == 0) {
			numBit = new Bit5(1);
			BitN data = new Bit35();
			data.setLong(value);
			dataBit = data;
		}
		else if(value >>> 42 == 0) {
			numBit = new Bit6(1);
			BitN data = new Bit42();
			data.setLong(value);
			dataBit = data;
		}
		else if(value >>> 49 == 0) {
			numBit = new Bit7(1);
			BitN data = new Bit49();
			data.setLong(value);
			dataBit = data;
		}
		else if(value >>> 56 == 0) {
			numBit = new Bit8(1);
			BitN data = new Bit56();
			data.setLong(value);
			dataBit = data;
		}
		else {
			throw new RuntimeException("overflowしました。格納できない値です。");
		}
	}
	/**
	 * 先頭の1bitを追記していって、データサイズを知りたい
	 * @param bit1
	 * @return
	 */
	public boolean addBit1(Bit1 bit1) {
		if(bit1.get() == 1) {
			// このタイミングでnumBitが決定します。
			switch(zeroCount) {
			case 0: numBit = new Bit1(1); break;
			case 1: numBit = new Bit2(1); break;
			case 2: numBit = new Bit3(1); break;
			case 3: numBit = new Bit4(1); break;
			case 4: numBit = new Bit5(1); break;
			case 5: numBit = new Bit6(1); break;
			case 6: numBit = new Bit7(1); break;
			case 7: numBit = new Bit8(1); break;
			default:
				throw new RuntimeException("ebmlとして不正なデータです。");
			}
			return false;
		}
		else {
			zeroCount ++;
		}
		return true;
	}
	/**
	 * データの基本bitを応答する
	 * @return
	 */
	protected Bit getDataBit() {
		switch(zeroCount) {
		case 0: dataBit = new Bit7();  break;
		case 1: dataBit = new Bit14(); break;
		case 2: dataBit = new Bit21(); break;
		case 3: dataBit = new Bit28(); break;
		case 4: dataBit = new Bit35(); break;
		case 5: dataBit = new Bit42(); break;
		case 6: dataBit = new Bit49(); break;
		case 7: dataBit = new Bit56(); break;
		default:
			throw new RuntimeException("ebmlとして不正なデータです。");
		}
		return dataBit;
	}
	protected Bit getNumBit() {
		return numBit;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(numBit.toString());
		data.append(dataBit.toString());
		return data.toString();
	}
}
