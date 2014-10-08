/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.flv1;

import java.nio.ByteBuffer;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit17;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * base for flv1 frame
 * @see http://hkpr.info/flash/swf/index.php?%E3%83%93%E3%83%87%E3%82%AA%2FSorenson%20H.263%20%E3%83%93%E3%83%83%E3%83%88%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%A0%E3%83%95%E3%82%A9%E3%83%BC%E3%83%9E%E3%83%83%E3%83%88
 * TODO temporalReference counter need to start from 0.(maybe suppress the warning on ffmpeg)
 * @author taktod
 */
public abstract class Flv1Frame extends VideoFrame {
	private final Bit17 pictureStartCode;
	private final Bit5  version;
	private final Bit8  temporalReference;
	private final Bit3  pictureSize;
	private final Bit   customWidth;
	private final Bit   customHeight;
	private final Bit2  pictureType;
	private final Bit1  deblockingFlag;
	private final Bit5  quantizer;
	private final Bit1  extraInformationFlag;
	private final Bit8  extraInformation;
	private final Bit   extra;
	private ByteBuffer buffer = null; // buffer to read layer.
	private int width, height;

	/**
	 * constructor
	 * @param pictureStartCode
	 * @param version
	 * @param temporalReference
	 * @param pictureSize
	 * @param customWidth
	 * @param customHeight
	 * @param width
	 * @param height
	 * @param pictureType
	 * @param deblockingFlag
	 * @param quantizer
	 * @param extraInformationFlag
	 * @param extraInformation
	 * @param extra
	 */
	public Flv1Frame(Bit17 pictureStartCode,
			Bit5 version, Bit8 temporalReference, Bit3 pictureSize,
			Bit customWidth, Bit customHeight,
			int width, int height, Bit2 pictureType, Bit1 deblockingFlag,
			Bit5 quantizer, Bit1 extraInformationFlag, Bit8 extraInformation, Bit extra) {
		this.pictureStartCode = pictureStartCode;
		this.version = version;
		this.temporalReference = temporalReference;
		this.pictureSize = pictureSize;
		this.customWidth = customWidth;
		this.customHeight = customHeight;
		this.pictureType = pictureType;
		this.deblockingFlag = deblockingFlag;
		this.quantizer = quantizer;
		this.extraInformationFlag = extraInformationFlag;
		this.extraInformation = extraInformation;
		this.extra = extra;
		
		this.width = width;
		this.height = height;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		if(buffer == null) {
			throw new Exception("data body is undefined yet.");
		}
		// TODO use BufferUtil.connect.
		// connect the holding data.
		BitConnector bitConnector = new BitConnector();
		ByteBuffer bitData = bitConnector.connect(pictureStartCode, version, temporalReference,
				pictureSize, customWidth, customHeight,
				pictureType, deblockingFlag, quantizer,
				extraInformationFlag, extraInformation, extra);
		int size = bitData.remaining() + buffer.remaining();
		// add buffer.
		ByteBuffer data = ByteBuffer.allocate(size);
		data.put(bitData);
		data.put(buffer.duplicate());
		data.flip();
		return data;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(super.getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// for minimumload there is nothing to do.
		super.setWidth(width);
		super.setHeight(height);
		super.setDts(0);
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("body buffer is undefined yet.");
		}
		BitConnector connector = new BitConnector();
		setData(BufferUtil.connect(connector.connect(pictureStartCode, version, temporalReference,
				pictureSize, customWidth, customHeight,
				pictureType, deblockingFlag, quantizer,
				extraInformationFlag, extraInformation, extra),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPts(long pts) {
		super.setPts(pts);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		// for flv1 getPacketBuffer = getData.
		return getData();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.FLV1;
	}
}
