package com.ttProject.frame.h265.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.h265.H265Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

public class PpsNut extends H265Frame {
	/** データ */
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 */
	public PpsNut(Bit1 forbiddenZeroBit,
			Bit6 nalUnitType,
			Bit6 nuhLayerId,
			Bit3 nuhTemporalIdPlus1) {
		super(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
