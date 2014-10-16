/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.util.BufferUtil;

/**
 * vprp
 * videoPropertiesHeader
 * @author taktod
 */
public class Vprp extends RiffSizeUnit {
	private Bit32 videoFormatToken;
	private Bit32 videoStandard;
	private Bit32 dwVerticalRefreshRate;
	private Bit32 dwHTotalInT;
	private Bit32 dwVTotalInLines;
	private Bit32 dwFrameAspctRatio;
	private Bit32 dwFrameWidthInPixels;
	private Bit32 dwFrameHeightInLines;
	private Bit32 nbFieldPerFrame;
	private List<VideoFieldDesc> videoFieldDescList = new ArrayList<VideoFieldDesc>();
	/**
	 * constructor
	 */
	public Vprp() {
		super(Type.vprp);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		BufferUtil.quickDispose(channel, getSize() - 8);
	}

	@Override
	protected void requestUpdate() throws Exception {
		
	}
	public static class VideoFieldDesc {
		private Bit32 compressedBMHeight;
		private Bit32 compressedBMWidth;
		private Bit32 validBMHeight;
		private Bit32 validBMWidth;
		private Bit32 validBMXOffset;
		private Bit32 validBMYOffset;
		private Bit32 videoXOffsetInT;
		private Bit32 videoYValidStartLine;
	}
}
