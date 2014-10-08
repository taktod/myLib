/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame;

import java.nio.ByteBuffer;

import com.ttProject.unit.IUnit;

/**
 * interface of frame
 * @author taktod
 */
public interface IFrame extends IUnit {
	/**
	 * ref the minimum unit of complete data.
	 * getData will return only frame needed.
	 * ex: SliceIDRFrame for h264
	 * getPackBuffer return sps pps and sliceIDR data.
	 * getData return sliceIDR data only.
	 * @return
	 */
	public ByteBuffer getPackBuffer() throws Exception;
	/**
	 * duration of each frame.
	 * (for audio, sampleNum / sampleRate)
	 * (for video, get from fps value)
	 */
	public float getDuration();
	/**
	 * ref the codecType
	 * @return
	 */
	public CodecType getCodecType();
	/**
	 * ref the private data.
	 * (h264 configData, aac decoderSpecificInfo, vorbis opus speex codecPrivate...)
	 * @return
	 */
	public ByteBuffer getPrivateData() throws Exception;
}
