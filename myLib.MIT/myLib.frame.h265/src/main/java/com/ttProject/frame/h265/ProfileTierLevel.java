/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265;

import org.apache.log4j.Logger;

import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit44;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * information on profileTierLevel.
 * this is in vps or sps.
 * @author taktod
 */
public class ProfileTierLevel {
	/** logger */
	private Logger logger = Logger.getLogger(ProfileTierLevel.class);
	private Bit2 generalProfileSpace = new Bit2();
	private Bit1 generalTierFlag = new Bit1();
	private Bit5 generalProfileIdc = new Bit5();
	private Bit1[] generalProfileCompatibilityFlags = new Bit1[32];
	{
		for(int i = 0;i < 32;i ++) {
			generalProfileCompatibilityFlags[i] = new Bit1();
		}
	}
	private Bit1 generalProgressiveSourceFlag = new Bit1();
	private Bit1 generalInterlacedSourceFlag = new Bit1();
	private Bit1 generalNonPackedConstraintFlag = new Bit1();
	private Bit1 generalFrameOnlyConstraintFlag = new Bit1();
	private Bit44 generalReservedZero44Bit = new Bit44();
	private Bit8 generalLevelIdc = new Bit8();
	private Bit1[] subLayerProfilePresentFlag = null;
	private Bit1[] subLayerLevelPresentFlag = null;
	public void minimumLoad(BitLoader loader, int maxSubLayersMinus1) throws Exception {
		logger.info("profileTierLevel:" + maxSubLayersMinus1);
		if(maxSubLayersMinus1 != 0) {
			throw new RuntimeException("unexpected information. I need the sample to improve.");
		}
		loader.load(generalProfileSpace, generalTierFlag, generalProfileIdc);
		loader.load(generalProfileCompatibilityFlags);
		loader.load(generalProgressiveSourceFlag,
				generalInterlacedSourceFlag,
				generalNonPackedConstraintFlag,
				generalFrameOnlyConstraintFlag,
				generalReservedZero44Bit,
				generalLevelIdc);
	}
}
