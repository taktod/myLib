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

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Ueg;
import com.ttProject.util.BufferUtil;

/**
 * base of slice frame
 * @author taktod
 */
public abstract class SliceFrame extends H264Frame {
	private Ueg firstMbInSlice        = new Ueg();
	private Ueg sliceType             = new Ueg();
	private Ueg pictureParameterSetId = new Ueg();
	private Bit extraBit              = null;
	
	/**
	 * constructor
	 * @param forbiddendZeroBit
	 * @param nalRefIdc
	 * @param type
	 */
	public SliceFrame(Bit1 forbiddenZeroBit, Bit2 nalRefIdc, Bit5 type) {
		super(forbiddenZeroBit, nalRefIdc, type);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(firstMbInSlice, sliceType, pictureParameterSetId);
		extraBit = loader.getExtraBit();
	}
	/**
	 * ref the header data of slice frame.
	 * @return
	 */
	protected ByteBuffer getSliceHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(firstMbInSlice, sliceType, pictureParameterSetId, extraBit);
	}
	public int getFirstMbInSlice() throws Exception {
		return firstMbInSlice.get();
	}
	/**
	 * get the size base nal buffer.
	 * ex: flv or mp4.
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getDataPackBuffer() throws Exception {
		BitConnector connector = new BitConnector();
		List<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
		for(H264Frame frame : getGroupFrameList()) {
			ByteBuffer targetBuffer = frame.getData();
			bufferList.add(connector.connect(new Bit32(targetBuffer.remaining())));
			bufferList.add(targetBuffer);
		}
		return BufferUtil.connect(bufferList);
	}
}
