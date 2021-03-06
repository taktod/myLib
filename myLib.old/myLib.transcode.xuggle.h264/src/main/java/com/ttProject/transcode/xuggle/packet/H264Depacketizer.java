/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.transcode.xuggle.packet;

import java.util.List;

import com.ttProject.media.Unit;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * packetからh264Frameを作成します。
 * h264のframeはこのままでは、timestampが決定しないので、データとして取り出すことができない。
 * カップリングするなにかを作る必要あり。
 * @author taktod
 */
public class H264Depacketizer implements IDepacketizer {
	@Override
	public List<Unit> getUnits(IStreamCoder encoder, IPacket packet)
			throws Exception {
		return null;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
