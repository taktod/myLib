/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit48;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

/**
 * h265のconfigData
 * mp4のhvcCのタグの中身がこれに相当します。
 * @author taktod
 * 01 01 60 00 00 00 80 00 00 00 00 00 5D F0 00 FC FD F8 F8 00 00 0F 03 20 00 01 00 18 40 01 0C 01 FF FF 01 60 00 00 03 00 80 00 00 03 00 00 03 00 5D 95 C0 90 21 00 01 00 29 42 01 01 01 60 00 00 03 00 80 00 00 03 00 00 03 00 5D A0 07 82 00 B4 59 65 79 24 DA F0 10 10 00 00 03 00 30 00 00 05 60 80 22 00 01 00 06 44 01 C1 73 D1 89
 */
public class ConfigData {
	/** ロガー */
	private Logger logger = Logger.getLogger(ConfigData.class);
	private H265FrameSelector selector = null;
	
	private Bit8  configurationVersion             = new Bit8();
	private Bit2  generalProfileSpace              = new Bit2();
	private Bit1  generalTierFlag                  = new Bit1();
	private Bit5  generalProfileIdc                = new Bit5();
	private Bit32 generalProfileCompatibilityFlags = new Bit32();
	private Bit48 generalConstraintIndicatorFlags  = new Bit48();
	private Bit8  generalLevelIdc                  = new Bit8();
	private Bit4  reserved1                        = new Bit4();
	private Bit12 minSpatialSegmentationIdc        = new Bit12();
	private Bit6  reserved2                        = new Bit6();
	private Bit2  parallelismType                  = new Bit2();
	private Bit6  reserved3                        = new Bit6();
	private Bit2  chromaFormat                     = new Bit2();
	private Bit5  reserved4                        = new Bit5();
	private Bit3  bitDepthLumaMinus8               = new Bit3();
	private Bit5  reserved5                        = new Bit5();
	private Bit3  bitDepthChromaMinus8             = new Bit3();
	private Bit16 avgFrameRate                     = new Bit16();
	private Bit2  constantFrameRate                = new Bit2();
	private Bit3  numTemporalLayers                = new Bit3();
	private Bit1  temporalIdNested                 = new Bit1();
	private Bit2  lengthSizeMinusOne               = new Bit2();
	private Bit8  numOfArrays                      = new Bit8();
	
		private Bit1 arrayCompleteness;
		private Bit1 reserved;
		private Bit6 nalUnitType;
		private Bit16 numNalus;
		
			private Bit16 nalUnitLength;

	private List<H265Frame> nalList = new ArrayList<H265Frame>();
	public void setSelector(H265FrameSelector selector) {
		this.selector = selector;
	}
	/**
	 * h265ConfigDataを解析する
	 * @param channel
	 * @throws Exception
	 */
	public void analyze(IReadChannel channel) throws Exception {
		ISelector selector = null;
		if(this.selector != null) {
			selector = this.selector;
		}
		else {
			selector = new H265FrameSelector();
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(configurationVersion, generalProfileSpace,
				generalTierFlag, generalProfileIdc, generalProfileCompatibilityFlags,
				generalConstraintIndicatorFlags, generalLevelIdc,
				reserved1, minSpatialSegmentationIdc,
				reserved2, parallelismType,
				reserved3, chromaFormat,
				reserved4, bitDepthLumaMinus8,
				reserved5, bitDepthChromaMinus8,
				avgFrameRate, constantFrameRate, numTemporalLayers,
				temporalIdNested, lengthSizeMinusOne, numOfArrays);
		logger.info(numOfArrays.get());
		for(int i = 0;i < numOfArrays.get();i ++) {
			arrayCompleteness = new Bit1();
			reserved          = new Bit1();
			nalUnitType       = new Bit6();
			numNalus          = new Bit16();
			loader.load(arrayCompleteness, reserved,
					nalUnitType, numNalus);
			logger.info(nalUnitType.get());
			logger.info(Type.getType(nalUnitType.get()));
			logger.info(numNalus.get());
			for(int j = 0;j < numNalus.get();j ++) {
				nalUnitLength = new Bit16();
				loader.load(nalUnitLength);
				logger.info(nalUnitLength.get());
				// ここから読み込むべきnalのサイズ
				ByteBuffer data = BufferUtil.safeRead(channel, nalUnitLength.get());
				logger.info(HexUtil.toHex(data, true));
				IReadChannel nalChannel = new ByteReadChannel(data);
				H265Frame nal = (H265Frame)selector.select(nalChannel);
				nalList.add(nal);
			}
		}
	}
	/*
	aligned(8) class HEVCDecoderConfigurationRecord {
		for (j=0; j < numOfArrays; j++) {
		bit(1) array_completeness;
		unsigned int(1) reserved = 0;
		unsigned int(6) NAL_unit_type;
		unsigned int(16) numNalus;
		for (i=0; i< numNalus; i++) {
		unsigned int(16) nalUnitLength;
		bit(8*nalUnitLength) nalUnit;
		}
		}
		}
	*/
}
