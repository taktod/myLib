package com.ttProject.media.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit7;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

public class DtsField {
	// 0010 XXX1 XXXX XXXX XXXX XXX1 XXXX XXXX XXXX XXX1
	private Bit4 signature;
	private long dts;
	public void analyze(IReadChannel ch) throws Exception {
		signature = new Bit4();
		Bit3 dts1 = new Bit3();
		Bit1 dtsFlag1 = new Bit1();
		Bit7 dts2 = new Bit7();
		Bit8 dts3 = new Bit8();
		Bit1 dtsFlag2 = new Bit1();
		Bit7 dts4 = new Bit7();
		Bit8 dts5 = new Bit8();
		Bit1 dtsFlag3 = new Bit1();
		Bit.bitLoader(ch, signature, dts1, dtsFlag1, dts2, dts3, dtsFlag2, dts4, dts5, dtsFlag3);
		if(dtsFlag1.get() != 0x01
		|| dtsFlag2.get() != 0x01
		|| dtsFlag3.get() != 0x01) {
			throw new Exception("セパレートフラグがおかしいです。");
		}
		dts = (long)(((dts1.get() & 0xFFL) << 30) | (dts2.get() << 23) | (dts3.get() << 15) | (dts4.get() << 8) | dts5.get());
	}
	public long getDts() {
		return dts;
	}
	public void setDts(long dts) {
		this.dts = dts;
	}
	public void setSignature(Bit4 signature) {
		this.signature = signature;
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(signature);
		list.add(new Bit3((int)(dts >>> 30)));
		list.add(new Bit1(1));
		list.add(new Bit7((int)(dts >>> 23)));
		list.add(new Bit8((int)(dts >>> 15)));
		list.add(new Bit1(1));
		list.add(new Bit7((int)(dts >>> 8)));
		list.add(new Bit8((int)(dts & 0xFF)));
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
