package com.ttProject.frame.h265.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.h265.SliceFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * TRAIL_Rのnal
 * h264でいうところのsliceか？
 * @author taktod
 */
public class TrailN extends SliceFrame {
	/**
	 * コンストラクタ
	 * @param forbiddenZeroBit
	 * @param nalUnitType
	 * @param nuhLayerId
	 * @param nuhTemporalIdPlus1
	 */
	public TrailN(Bit1 forbiddenZeroBit,
			Bit6 nalUnitType,
			Bit6 nuhLayerId,
			Bit3 nuhTemporalIdPlus1) {
		super(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
}
