/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.h265.H265Frame;
import com.ttProject.frame.h265.ProfileTierLevel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Ueg;

/**
 * sps
 * @author taktod
 */
public class SpsNut extends H265Frame {
	/** logger */
	private Logger logger = Logger.getLogger(SpsNut.class);
	private Bit4 spsVideoParameterSetId = new Bit4();
	private Bit3 spsMaxSubLayersMinus1 = new Bit3();
	private Bit1 spsTemporalIdNestingFlag = new Bit1();
	private ProfileTierLevel profileTierLevel = new ProfileTierLevel();
	private Ueg spsSeqParameterSetId = new Ueg();
	private Ueg chromaFormatIdc = new Ueg();
	private Bit1 separateColourPlaneFlag = null;
	private Ueg picWidthInLumaSamples = new Ueg();
	private Ueg picHeightInLumaSamples = new Ueg();
	private Bit1 conformanceWindowFlag = new Bit1();
	/** data */
	private ByteBuffer buffer = null;
	/**
	 * constructor
	 * @param forbiddenZeroBit
	 * @param nalUnitType
	 * @param nuhLayerId
	 * @param nuhTemporalIdPlus1
	 */
	public SpsNut(Bit1 forbiddenZeroBit,
			Bit6 nalUnitType,
			Bit6 nuhLayerId,
			Bit3 nuhTemporalIdPlus1) {
		super(forbiddenZeroBit, nalUnitType, nuhLayerId, nuhTemporalIdPlus1);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.setEmulationPreventionFlg(true);
		loader.load(spsVideoParameterSetId, spsMaxSubLayersMinus1,
				spsTemporalIdNestingFlag);
		// 96 / 8 = 12;
		profileTierLevel.minimumLoad(loader, spsMaxSubLayersMinus1.get());
		// now load the sps.
		loader.load(spsSeqParameterSetId, chromaFormatIdc);
		if(chromaFormatIdc.get() == 3) {
			separateColourPlaneFlag = new Bit1();
			loader.load(separateColourPlaneFlag);
		}
		loader.load(picWidthInLumaSamples, picHeightInLumaSamples,
				conformanceWindowFlag);
		logger.info("width:" + picWidthInLumaSamples.get());
		logger.info("height:" + picHeightInLumaSamples.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	public int getWidth() {
		return picWidthInLumaSamples.get();
	}
	/**
	 * {@inheritDoc}
	 */
	public int getHeight() {
		return picHeightInLumaSamples.get();
	}
}
