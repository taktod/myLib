package com.ttProject.frame.h265;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.h265.type.PPS_NUT_Frame;
import com.ttProject.frame.h265.type.SPS_NUT_Frame;
import com.ttProject.frame.h265.type.VPS_NUT_Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

public class H265FrameSelector extends VideoSelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(H265FrameSelector.class);
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		Bit1 forbiddenZeroBit = new Bit1();
		Bit6 nalUnitType = new Bit6();
		Bit6 nuhLayerId = new Bit6();
		Bit3 nuhTemporalIdPlus1 = new Bit3();
		loader.load(forbiddenZeroBit, nalUnitType,
				nuhLayerId, nuhTemporalIdPlus1);
		H265Frame frame = null;
		switch(Type.getType(nalUnitType.get())) {
		case VPS_NUT:
			frame = new VPS_NUT_Frame(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			break;
		case SPS_NUT:
			frame = new SPS_NUT_Frame(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			break;
		case PPS_NUT:
			frame = new PPS_NUT_Frame(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			break;
		default:
			throw new Exception("想定外のframeを読み込みました。:" + Type.getType(nalUnitType.get()));
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
