/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.flv1;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.flv1.frame.DisposableInterFrame;
import com.ttProject.media.flv1.frame.InterFrame;
import com.ttProject.media.flv1.frame.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * flvの内容解析を実施する。
 * @author taktod
 */
public class FrameAnalyzer implements IFrameAnalyzer {
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		BitLoader bitLoader = new BitLoader(ch);
		Bit8 pictureStartCode1 = new Bit8();
		Bit8 pictureStartCode2 = new Bit8();
		Bit1 pictureStartCode3 = new Bit1();
		Bit5 version = new Bit5();
		Bit8 temporalReference = new Bit8();
		Bit3 pictureSize = new Bit3();
		bitLoader.load(pictureStartCode1, pictureStartCode2, pictureStartCode3,
				version, temporalReference, pictureSize);
		if(pictureStartCode1.get() != 0 ||
				pictureStartCode2.get() != 0 ||
				pictureStartCode3.get() != 1) {
			throw new Exception("開始タグが想定外です。");
		}
		int width = 0;
		int height = 0;
		Bit8 width1 = null;
		Bit8 width2 = null;
		Bit8 height1 = null;
		Bit8 height2 = null;
		switch(pictureSize.get()) {
		case 0: // custom1
			width1 = new Bit8();
			height1 = new Bit8();
			bitLoader.load(width1, height1);
			width = width1.get();
			height = height1.get();
			break;
		case 1: // custom2
			width1 = new Bit8();
			width2 = new Bit8();
			height1 = new Bit8();
			height2 = new Bit8();
			bitLoader.load(width1, width2, height1, height2);
			width = ((width1.get() << 8) | width2.get());
			height = ((height1.get() << 8) | height2.get());
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
			throw new Exception("pictureSizeがreservedになっていました。");
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
		switch(pictureType.get()) {
		case 0: // intraFrame
			return new IntraFrame(pictureStartCode1, pictureStartCode2, pictureStartCode3, version, temporalReference, pictureSize, width, height, pictureType, deblockingFlag, quantizer, extraInformationFlag, extraInformation);
		case 1: // interFrame
			return new InterFrame(pictureStartCode1, pictureStartCode2, pictureStartCode3, version, temporalReference, pictureSize, width, height, pictureType, deblockingFlag, quantizer, extraInformationFlag, extraInformation);
		case 2: // disposableInterFrame
			return new DisposableInterFrame(pictureStartCode1, pictureStartCode2, pictureStartCode3, version, temporalReference, pictureSize, width, height, pictureType, deblockingFlag, quantizer, extraInformationFlag, extraInformation);
		case 3: // reserved
		default:
			throw new Exception("知らないフレームタイプです");
		}
	}
}
