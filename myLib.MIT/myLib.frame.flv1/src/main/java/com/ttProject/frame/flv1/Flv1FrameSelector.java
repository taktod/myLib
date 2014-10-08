/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.flv1;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.flv1.type.DisposableInterFrame;
import com.ttProject.frame.flv1.type.InterFrame;
import com.ttProject.frame.flv1.type.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit17;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * selector for flv1 frame.
 * @author taktod
 */
public class Flv1FrameSelector extends VideoSelector {
	/**
	 * select flv1 frame.
	 * @param channel (expect the channel has only 1 frame.)
	 * @return
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() - channel.position() < 4) {
			// need more than 32 bit(4byte).
			return null;
		}
		BitLoader bitLoader = new BitLoader(channel);
		Bit17 pictureStartCode = new Bit17();
		Bit5 version = new Bit5();
		Bit8 temporalReference = new Bit8();
		Bit3 pictureSize = new Bit3();
		bitLoader.load(pictureStartCode,
				version, temporalReference, pictureSize);
		if(pictureStartCode.get() != 1) {
			throw new Exception("picture start code is unexpected. currept?");
		}
		int width = 0;
		int height = 0;
		Bit customWidth = null;
		Bit customHeight = null;
		switch(pictureSize.get()) {
		case 0: // custom1
			customWidth = new Bit8();
			customHeight = new Bit8();
			bitLoader.load(customWidth, customHeight);
			width = customWidth.get();
			height = customHeight.get();
			break;
		case 1: // custom2
			customWidth = new Bit16();
			customHeight = new Bit16();
			bitLoader.load(customWidth, customHeight);
			width = customWidth.get();
			height = customHeight.get();
			break;
		case 2: // CIF
			width = 352;
			height = 288;
			break;
		case 3: // QCIF
			width = 176;
			height = 144;
			break;
		case 4: // SQCIF
			width =  128;
			height = 96;
			break;
		case 5: // 320x240
			width = 320;
			height = 240;
			break;
		case 6: // 160x120
			width = 160;
			height = 120;
			break;
		case 7: // reserved
			throw new Exception("picture size is reserved.");
		}
		Bit2 pictureType = new Bit2();
		Bit1 deblockingFlag = new Bit1();
		Bit5 quantizer = new Bit5();
		Bit1 extraInformationFlag = new Bit1();
		bitLoader.load(pictureType, deblockingFlag, quantizer, extraInformationFlag);
		Bit8 extraInformation = null;
		if(extraInformationFlag.get() == 1) {
			extraInformation = new Bit8();
			bitLoader.load(extraInformation);
		}
		Flv1Frame frame = null;
		switch(pictureType.get()) {
		case 0: // intraFrame
			frame = new IntraFrame(pictureStartCode, version, temporalReference,
					pictureSize, customWidth, customHeight, width, height,
					pictureType, deblockingFlag, quantizer, extraInformationFlag,
					extraInformation, bitLoader.getExtraBit());
			break;
		case 1: // interFrame
			frame = new InterFrame(pictureStartCode, version, temporalReference,
					pictureSize, customWidth, customHeight, width, height,
					pictureType, deblockingFlag, quantizer, extraInformationFlag,
					extraInformation, bitLoader.getExtraBit());
			break;
		case 2: // disposableInterFrame
			frame = new DisposableInterFrame(pictureStartCode, version, temporalReference,
					pictureSize, customWidth, customHeight, width, height,
					pictureType, deblockingFlag, quantizer, extraInformationFlag,
					extraInformation, bitLoader.getExtraBit());
			break;
		case 3: // reserved
		default:
			throw new Exception("unexpected flv1 frame/");
		}
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
