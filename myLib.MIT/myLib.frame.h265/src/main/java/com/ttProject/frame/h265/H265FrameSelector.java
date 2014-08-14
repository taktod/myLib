/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.h265.type.IdrWRadl;
import com.ttProject.frame.h265.type.PpsNut;
import com.ttProject.frame.h265.type.SpsNut;
import com.ttProject.frame.h265.type.TrailN;
import com.ttProject.frame.h265.type.TrailR;
import com.ttProject.frame.h265.type.VpsNut;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

public class H265FrameSelector extends VideoSelector {
	/** ロガー */
//	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(H265FrameSelector.class);
	private VpsNut vps = null;
	private SpsNut sps = null;
	private PpsNut pps = null;
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		BitLoader loader = new BitLoader(channel);
		loader.setEmulationPreventionFlg(true);
		Bit1 forbiddenZeroBit = new Bit1();
		Bit6 nalUnitType = new Bit6();
		Bit6 nuhLayerId = new Bit6();
		Bit3 nuhTemporalIdPlus1 = new Bit3();
		loader.load(forbiddenZeroBit, nalUnitType,
				nuhLayerId, nuhTemporalIdPlus1);
		H265Frame frame = null;
		logger.info("frameの読み込みを開始します。:type:" + Type.getType(nalUnitType.get()));
		switch(Type.getType(nalUnitType.get())) {
		case VPS_NUT:
			frame = new VpsNut(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			vps = (VpsNut)frame;
			break;
		case SPS_NUT:
			frame = new SpsNut(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			sps = (SpsNut)frame;
			break;
		case PPS_NUT:
			frame = new PpsNut(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			pps = (PpsNut)frame;
			break;
		case IDR_W_RADL:
			frame = new IdrWRadl(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			break;
		case TRAIL_R:
			frame = new TrailR(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			break;
		case TRAIL_N:
			frame = new TrailN(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
			break;
		default:
			throw new Exception("想定外のframeを読み込みました。:" + Type.getType(nalUnitType.get()));
		}
		if(!(frame instanceof SpsNut)
		&& !(frame instanceof VpsNut)
		&& !(frame instanceof PpsNut)) {
			frame.setSps(sps);
			frame.setPps(pps);
			frame.setVps(vps);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
