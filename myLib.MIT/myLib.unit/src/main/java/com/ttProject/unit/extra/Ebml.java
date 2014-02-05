package com.ttProject.unit.extra;

import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit14;
import com.ttProject.unit.extra.bit.Bit21;
import com.ttProject.unit.extra.bit.Bit28;
import com.ttProject.unit.extra.bit.Bit35;
import com.ttProject.unit.extra.bit.Bit42;
import com.ttProject.unit.extra.bit.Bit49;
import com.ttProject.unit.extra.bit.Bit56;
import com.ttProject.unit.extra.bit.Bit7;

public class Ebml extends Bit {
	private byte zeroCount = 0;
	private Bit dataBit = null;
	/**
	 * コンストラクタ
	 * @param count
	 */
	public Ebml() {
		super(0);
	}
	public long getLong() {
		if(dataBit instanceof BitN) {
			return ((BitN) dataBit).getLong();
		}
		else {
			return dataBit.get();
		}
	}
	public void setLong(long data) {
	}
	/**
	 * 先頭の1bitを追記していって、データサイズを知りたい
	 * @param bit1
	 * @return
	 */
	public boolean addBit1(Bit1 bit1) {
		if(bit1.get() == 1) {
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
	public Bit getDataBit() throws Exception {
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
			throw new Exception("ebmlとして不正なデータです。");
		}
		return dataBit;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
