package com.ttProject.container.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit15;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;

public class DtsField {
	// 0010 XXX1 XXXX XXXX XXXX XXX1 XXXX XXXX XXXX XXX1
	private Bit4 signature = new Bit4(2);
	private long dts;
	public void load(IReadChannel ch) throws Exception {
		signature = new Bit4();
		Bit3 dts1 = new Bit3();
		Bit1 dtsFlag1 = new Bit1();
		Bit15 dts2 = new Bit15();
		Bit1 dtsFlag2 = new Bit1();
		Bit15 dts3 = new Bit15();
		Bit1 dtsFlag3 = new Bit1();
		BitLoader bitLoader = new BitLoader(ch);
		bitLoader.load(signature, dts1, dtsFlag1, dts2, dtsFlag2, dts3, dtsFlag3);
		if(dtsFlag1.get() != 0x01
		|| dtsFlag2.get() != 0x01
		|| dtsFlag3.get() != 0x01) {
			throw new Exception("セパレートフラグがおかしいです。");
		}
		dts = (long)(((dts1.get() & 0xFFL) << 30) | (dts2.get() << 15) | (dts3.get()));
	}
	public long getDts() {
		return dts;
	}
	public void setDts(long dts) {
		this.dts = dts;
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(signature);
		list.add(new Bit3((int)(dts >>> 30)));
		list.add(new Bit1(1));
		list.add(new Bit15((int)(dts >>> 15)));
		list.add(new Bit1(1));
		list.add(new Bit15((int)(dts)));
		list.add(new Bit1(1));
		return list;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("DtsField:");
		data.append(" dts:").append(Long.toHexString(dts)).append("(").append(dts / 90000f).append(")");
		return data.toString();
	}
}
