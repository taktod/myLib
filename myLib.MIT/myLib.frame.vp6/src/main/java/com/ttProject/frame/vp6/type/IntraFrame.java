package com.ttProject.frame.vp6.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vp6.Vp6Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN.Bit16;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

public class IntraFrame extends Vp6Frame {
	private Bit5 version = new Bit5();
	private Bit2 version2 = new Bit2();
	private Bit1 interlace = new Bit1();
	private Bit16 offset = null; // 16bit
	private Bit8 dimY = new Bit8(); // x16で縦幅
	private Bit8 dimX = new Bit8(); // x16で横幅
	private Bit8 renderY = new Bit8(); // x16で縦幅
	private Bit8 renderX = new Bit8(); // x16で横幅
	private ByteBuffer buffer = null;
	public IntraFrame(Bit1 frameMode, Bit6 qp, Bit1 marker) {
		super(frameMode, qp, marker);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(version, version2, interlace);
		if(version2.get() == 0 || getMarker().get() == 1) {
			offset = new Bit16();
			loader.load(offset);
		}
		loader.load(dimY, dimX, renderY, renderX);
		System.out.println(dimX.get() + "x" + dimY.get());
		super.setWidth(dimX.get() * 16);
		super.setHeight(dimY.get() * 16);
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(super.getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("本体データが設定されていません。");
		}
		BitConnector connector = new BitConnector();
		setData(BufferUtil.connect(getHeaderBuffer(),
				connector.connect(version, version2,
						interlace, offset,
						dimY, dimX, renderY, renderX),
				buffer));
	}
	public Bit2 getVersion2() {
		return version2;
	}
}
