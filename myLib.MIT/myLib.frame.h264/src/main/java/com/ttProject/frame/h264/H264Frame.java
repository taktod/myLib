/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit5;

/**
 * base of h264 frame
 * @author taktod
 */
public abstract class H264Frame extends VideoFrame {
	private final Bit1 forbiddenZeroBit;
	private final Bit2 nalRefIdc;
	private final Bit5 type;

	/** sps */
	private SequenceParameterSet sps = null;
	/** pps */
	private PictureParameterSet pps = null;
	/** frame list. */
	private List<H264Frame> frameList = null;
	/**
	 * constructor
	 * @param forbiddenZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public H264Frame(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		this.forbiddenZeroBit = forbiddenZeroBit;
		this.nalRefIdc = nalRefIdc;
		this.type = type;
	}
	/**
	 * ref the typeBuffer (header infromation)
	 * @return
	 */
	protected ByteBuffer getTypeBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(forbiddenZeroBit,
				nalRefIdc, type);
	}
	/**
	 * set the pps
	 * @param pps
	 */
	public void setPps(PictureParameterSet pps) {
		this.pps = pps;
	}
	/**
	 * set the sps
	 * @param sps
	 */
	public void setSps(SequenceParameterSet sps) {
		this.sps = sps;
		if(sps != null) {
			setWidth(sps.getWidth());
			setHeight(sps.getHeight());
		}
	}
	/**
	 * ref sps
	 * @return
	 */
	public SequenceParameterSet getSps() {
		return sps;
	}
	/**
	 * ref pps
	 * @return
	 */
	public PictureParameterSet getPps() {
		return pps;
	}
	public void addFrame(H264Frame frame) {
		if(frameList == null) {
			frameList = new ArrayList<H264Frame>();
		}
		frameList.add(frame);
	}
	public List<H264Frame> getGroupFrameList() {
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.H264;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPrivateData() throws Exception {
		if(sps == null || pps == null) {
			throw new Exception("sps or pps is undefined.");
		}
		ConfigData configData = new ConfigData();
		return configData.makeConfigData(sps, pps);
	}
}
