package com.ttProject.media.vp6.frame;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit6;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.vp6.Frame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * キーフレーム
 * @author taktod
 */
public class IntraFrame extends Frame {
	private Bit5 version;
	private Bit2 version2;
	private Bit1 interlace;

	private short offset; // 16bit

	private Bit8 dimY; // x16で縦幅
	private Bit8 dimX; // x16で横幅
	private Bit8 renderY; // x16で縦幅
	private Bit8 renderX; // x16で横幅
	/**
	 * コンストラクタ
	 * @param frameMode
	 * @param qp
	 * @param marker
	 */
	public IntraFrame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		super(frameMode, qp, marker);
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
		BitLoader bitLoader = new BitLoader(ch);
		version = new Bit5();
		version2 = new Bit2();
		interlace = new Bit1();
		bitLoader.load(version, version2, interlace);
		if(getMarker().get() == 1 || version2.get() == 0) {
			Bit8 offset1 = new Bit8();
			Bit8 offset2 = new Bit8();
			bitLoader.load(offset1, offset2);
			offset = (short)((offset1.get() << 8) | offset2.get());
		}
		dimY = new Bit8();
		dimX = new Bit8();
		renderY = new Bit8();
		renderX = new Bit8();
		bitLoader.load(dimY, dimX, renderY, renderX);
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(getClass().getSimpleName());
		data.append(" width:").append(renderX.get() * 16);
		data.append(" height:").append(renderY.get() * 16);
		return data.toString();
	}
}
