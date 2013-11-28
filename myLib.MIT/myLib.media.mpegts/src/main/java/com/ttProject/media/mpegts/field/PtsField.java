package com.ttProject.media.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit7;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.nio.channels.IReadChannel;

public class PtsField {
	// 0010 XXX1 XXXX XXXX XXXX XXX1 XXXX XXXX XXXX XXX1
	// 0011 XXX1 XXXX XXXX XXXX XXX1 XXXX XXXX XXXX XXX1
	// xの部分をつなぎ合わせたらptsデータとなります。
	private Bit4 signature;
	private long pts; // 90.0fで割り算したら、ミリ秒データになる。
	public void analyze(IReadChannel ch) throws Exception {
		signature = new Bit4();
		Bit3 pts1 = new Bit3();
		Bit1 ptsFlag1 = new Bit1();
		Bit7 pts2 = new Bit7();
		Bit8 pts3 = new Bit8();
		Bit1 ptsFlag2 = new Bit1();
		Bit7 pts4 = new Bit7();
		Bit8 pts5 = new Bit8();
		Bit1 ptsFlag3 = new Bit1();
		BitLoader bitLoader = new BitLoader(ch);
		bitLoader.load(signature, pts1, ptsFlag1, pts2, pts3, ptsFlag2, pts4, pts5, ptsFlag3);
		if(ptsFlag1.get() != 0x01
		|| ptsFlag2.get() != 0x01
		|| ptsFlag3.get() != 0x01) {
			throw new Exception("セパレートフラグがおかしいです。");
		}
		pts = (long)(((pts1.get() & 0xFFL) << 30) | (pts2.get() << 23) | (pts3.get() << 15) | (pts4.get() << 8) | pts5.get());
	}
	public long getPts() {
		return pts;
	}
	public void setPts(long pts) {
		this.pts = pts;
	}
	public void setSignature(Bit4 signature) {
		this.signature = signature;
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(signature);
		list.add(new Bit3((int)(pts >>> 30)));
		list.add(new Bit1(1));
		list.add(new Bit7((int)(pts >>> 23)));
		list.add(new Bit8((int)(pts >>> 15)));
		list.add(new Bit1(1));
		list.add(new Bit7((int)(pts >>> 8)));
		list.add(new Bit8((int)(pts & 0xFF)));
		list.add(new Bit1(1));
		return list;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("PtsField:");
		data.append(" pts:").append(Long.toHexString(pts)).append("(").append(pts / 90000f).append(")");
		return data.toString();
	}
}
