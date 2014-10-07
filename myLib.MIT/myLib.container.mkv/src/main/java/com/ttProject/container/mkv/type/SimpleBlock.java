/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.Lacing;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.frame.vp8.Vp8Frame;
import com.ttProject.frame.vp9.Vp9Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.util.BufferUtil;

/**
 * SimpleBlock
 * data sample.
 * A3 44 B4 81 00 00 80 00 00 02 6C ...
 *  A3[SimpleBlock]
 *  44 B4[tag size(ebml)]
 * -- already programmed in mkvTag.
 *  81[trackId(ebml)]
 *  00 00[timestamp diff(16bit)]
 *  1000 0000
 *  . keyFrame flag
 *   ... reserved0
 *       . is invisible frame? 1:invisible
 *        .. lacing(for frame dividing.*1)
 *          . discardable:what?
 * *1:for h264, detailed data is separated by h264 nal(sizeNal)
 * @see http://matroska.org/technical/specs/index.html#simpleblock_structure
 * @author taktod
 */
public class SimpleBlock extends MkvBlockTag {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SimpleBlock.class);
	private Bit1 keyFrameFlag       = new Bit1();
	private Bit3 reserved           = new Bit3();
	private Bit1 invisibleFrameFlag = new Bit1();
	private Bit2 lacing             = new Bit2();
	private Bit1 discardableFlag    = new Bit1();
	/**
	 * constructor
	 * @param size
	 */
	public SimpleBlock(EbmlValue size) {
		super(Type.SimpleBlock, size);
	}
	/**
	 * constructor
	 */
	public SimpleBlock() {
		this(new EbmlValue());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(keyFrameFlag, reserved, invisibleFrameFlag, lacing, discardableFlag);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Lacing getLacingType() throws Exception {
		return Lacing.getType(lacing.get());
	}
	public boolean isKeyFrame() {
		return keyFrameFlag.get() == 1;
	}
	public boolean isInvisibleFrame() {
		return invisibleFrameFlag.get() == 1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getRemainedSize() {
		return getMkvSize() - (getTrackId().getBitCount() + 24) / 8;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// here we need to make up the file.
		BitConnector connector = new BitConnector();
		ByteBuffer buffer = connector.connect(getTrackId(), getTimestampDiff(),
				keyFrameFlag, reserved, invisibleFrameFlag, lacing, discardableFlag);
		// from here, frame body.
		IFrame frame = getFrame();
		switch(frame.getCodecType()) {
		case AAC:
			AacFrame aacFrame = (AacFrame)frame;
			buffer = BufferUtil.connect(buffer, aacFrame.getBuffer());
			break;
		case H264:
			// use data nal.
			if(frame instanceof SliceFrame) {
				SliceFrame sliceFrame = (SliceFrame) frame;
				buffer = BufferUtil.connect(buffer, sliceFrame.getDataPackBuffer());
			}
			else {
				throw new Exception("only sliceFrame is supported for h264 data.");
			}
			break;
//		case H265:
		default:
			buffer = BufferUtil.connect(buffer, frame.getData());
			break;
		}
		getTagSize().set(buffer.remaining());
		buffer = BufferUtil.connect(connector.connect(getTagId(), getTagSize()), buffer);
		setSize(buffer.remaining());
		setData(buffer);
	}
	/**
	 * add frame
	 * @param trackId
	 * @param frame
	 */
	public void addFrame(int trackId, IFrame frame, int timestampDiff) throws Exception {
		super.setPts(timestampDiff);
		super.setTimebase(1000); // TODO this timebase is depend on the trackEntry setting.
		super.getTrackId().set(trackId);
		super.addFrame(frame);
		getTimestampDiff().set(timestampDiff);
		// audio is treated as KeyFrame.
		if(frame instanceof IAudioFrame) {
			keyFrameFlag.set(1);
		}
		else if(frame instanceof IVideoFrame) {
			IVideoFrame vFrame = (IVideoFrame)frame;
			if(vFrame.isKeyFrame()) {
				keyFrameFlag.set(1);
			}
			else {
				keyFrameFlag.set(0);
			}
			// TODO vp8 and vp9 need to put invisible for invisible frames.
			// block tag do have invisible setting.
			switch(frame.getCodecType()) {
			case VP8:
				@SuppressWarnings("unused")
				Vp8Frame vp8Frame = (Vp8Frame)frame;
				break;
			case VP9:
				@SuppressWarnings("unused")
				Vp9Frame vp9Frame = (Vp9Frame)frame;
				break;
			default:
				break;
			}
		}
		// for the lacing, I got a sample with laced mp3, however, nolaced mp3 also works. so later fix to use lacing if necessary.
		super.update();
	}
}
