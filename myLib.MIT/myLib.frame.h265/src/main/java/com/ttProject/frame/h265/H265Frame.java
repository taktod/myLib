/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h265;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.h265.type.PpsNut;
import com.ttProject.frame.h265.type.SpsNut;
import com.ttProject.frame.h265.type.VpsNut;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * h265のframe
 * @author taktod
 */
public abstract class H265Frame extends VideoFrame {
	private final Bit1 forbiddenZeroBit;
	private final Bit6 nalUnitType;
	private final Bit6 nuhLayerId;
	private final Bit3 nuhTemporalIdPlus1;
	
	// 3点セット
	private VpsNut vps = null;
	private SpsNut sps = null;
	private PpsNut pps = null;
	/** 複数フレームで同一データになる場合のフレームリスト */
	private List<H265Frame> frameList = null;
	public H265Frame(Bit1 forbiddenZeroBit,
			Bit6 nalUnitType,
			Bit6 nuhLayerId,
			Bit3 nuhTemporalIdPlus1) {
		this.forbiddenZeroBit = forbiddenZeroBit;
		this.nalUnitType = nalUnitType;
		this.nuhLayerId = nuhLayerId;
		this.nuhTemporalIdPlus1 = nuhTemporalIdPlus1;
	}
	public void setSps(SpsNut sps) {
		this.sps = sps;
		if(sps != null) {
			super.setWidth(sps.getWidth());
			super.setHeight(sps.getHeight());
		}
	}
	public SpsNut getSps() {
		return sps;
	}
	public void setPps(PpsNut pps) {
		this.pps = pps;
	}
	public PpsNut getPps() {
		return pps;
	}
	public void setVps(VpsNut vps) {
		this.vps = vps;
	}
	public VpsNut getVps() {
		return vps;
	}
	public void addFrame(H265Frame frame) {
		if(frameList == null) {
			frameList = new ArrayList<H265Frame>();
		}
		frameList.add(frame);
	}
	public List<H265Frame> getGroupFrameList() {
		return frameList;
	}
	public boolean isFirstNal() {
		if(frameList == null) {
			return false;
		}
		if(frameList.get(0).hashCode() != this.hashCode()) {
			return false;
		}
		return true;
	}
}
