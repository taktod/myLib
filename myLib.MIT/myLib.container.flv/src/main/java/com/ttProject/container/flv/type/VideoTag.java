/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.FlvCodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.frame.flv1.type.DisposableInterFrame;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.DataNalAnalyzer;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.H264FrameSelector;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.vp6.Vp6Frame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * videoTag
 * @author taktod
 */
public class VideoTag extends FlvTag {
	/** logger */
	private Logger logger = Logger.getLogger(VideoTag.class);
	private Bit4 frameType = new Bit4();
	private Bit4 codecId   = new Bit4();
	
	private Bit4  horizontalAdjustment = null; // vp6 only
	private Bit4  verticalAdjustment   = null; // vp6 only
	private Bit32 offsetToAlpha        = null; // vp6a only
	private Bit8  packetType           = null; // avc only
	private Bit24 dts                  = null; // avc only

	private ByteBuffer frameBuffer = null; // frameBuffer
	private ByteBuffer alphaData   = null; // alphaData for vp6a
	private IVideoFrame   frame         = null; // targetFrame.
	private VideoAnalyzer frameAnalyzer = null;
	private boolean frameAppendFlag     = false; // flg for frame append.
	/**
	 * constructor
	 * @param tagType
	 */
	public VideoTag(Bit8 tagType) {
		super(tagType);
	}
	/**
	 * constructor
	 */
	public VideoTag() {
		this(new Bit8(0x09));
	}
	/**
	 * set frameAnalyzer
	 * @param analyzer
	 */
	public void setFrameAnalyzer(VideoAnalyzer analyzer) {
		this.frameAnalyzer = analyzer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(codecId != null) {
			BitLoader loader = null;
			switch(getCodec()) {
			case H264:
				channel.position(getPosition() + 16);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 16 - 4);
				if(packetType.get() == 0) {
					if(frameAnalyzer == null || !(frameAnalyzer instanceof DataNalAnalyzer)) {
						throw new Exception("frameAnalyzer is not suitable for h264 flv.");
					}
					DataNalAnalyzer dataNalAnalyzer = (DataNalAnalyzer)frameAnalyzer;
					ConfigData configData = new ConfigData();
					configData.setSelector((H264FrameSelector)frameAnalyzer.getSelector());
					configData.analyzeData(new ByteReadChannel(frameBuffer));
					dataNalAnalyzer.setConfigData(configData);
				}
				break;
			case ON2VP6:
				horizontalAdjustment = new Bit4();
				verticalAdjustment = new Bit4();
				loader = new BitLoader(channel);
				loader.load(horizontalAdjustment, verticalAdjustment);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 13 - 4);
				break;
			case ON2VP6_ALPHA:
				offsetToAlpha = new Bit32();
				loader = new BitLoader(channel);
				loader.load(offsetToAlpha);
				int offset = offsetToAlpha.get();
				frameBuffer = BufferUtil.safeRead(channel, offset);
				alphaData = BufferUtil.safeRead(channel, getSize() - 16 - 4 - offset);
				break;
			default:
				channel.position(getPosition() + 12);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 12 - 4);
				break;
			}
		}
		// check the prevTagSize.
		if(getPrevTagSize() != BufferUtil.safeRead(channel, 4).getInt()) {
			throw new Exception("end size data is incorrect.");
		}
	}
	/**
	 * ref the codec
	 * @return
	 */
	public FlvCodecType getCodec() {
		return FlvCodecType.getVideoCodecType(codecId.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		if(getSize() == 15) {
			// data size can be 0.(h264 end tag and so on...)
			logger.warn("get the no data tag.");
			return;
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(frameType, codecId);
		if(getCodec() == FlvCodecType.H264) {
			// load the h264 extra data.
			packetType = new Bit8();
			dts = new Bit24();
			loader.load(packetType, dts);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null && frame == null) {
			throw new Exception("frame data is undefined.");
		}
		ByteBuffer frameBuffer = null;
		if(frameAppendFlag) {
			// in the case of frame append.(re-establish the data.)
			IVideoFrame codecCheckFrame = frame;
			if(frame instanceof VideoMultiFrame) {
				codecCheckFrame = ((VideoMultiFrame) frame).getFrameList().get(0);
			}
//			frameType;
//			codecId;
			int sizeEx = 0;
			if(codecCheckFrame instanceof Flv1Frame) {
				codecId.set(FlvCodecType.getVideoCodecNum(FlvCodecType.FLV1));
				sizeEx = 0;
			}
			else if(codecCheckFrame instanceof Vp6Frame) {
				// TODO need to think about vp6a
				horizontalAdjustment = new Bit4();
				verticalAdjustment = new Bit4();
				codecId.set(FlvCodecType.getVideoCodecNum(FlvCodecType.ON2VP6));
				sizeEx = 1;
			}
			else if(codecCheckFrame instanceof H264Frame) {
				codecId.set(FlvCodecType.getVideoCodecNum(FlvCodecType.H264));
				packetType = new Bit8(1);
				dts = new Bit24((int)(1.0D * frame.getDts() / frame.getTimebase() * 1000));
				sizeEx = 4;
			}
			else {
				throw new Exception("unsuitable frame for flv.:" + frame);
			}
			if(frame instanceof DisposableInterFrame) {
				frameType.set(3);
			}
			else {
				if(frame.isKeyFrame()) {
					frameType.set(1);
				}
				else {
					frameType.set(2);
				}
			}
			frameBuffer = getFrameBuffer();
			// need to update pts, timebase, and size
			setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
			setTimebase(1000);
			setSize(11 + 1 + sizeEx + frameBuffer.remaining() + 4);
		}
		else {
			// no frame append.
			frameBuffer = getFrameBuffer();
		}
		BitConnector connector = new BitConnector();
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer videoInfoBuffer = connector.connect(
				frameType, codecId, 
				horizontalAdjustment, verticalAdjustment, // vp6
				offsetToAlpha, // vp6a
				packetType, dts // avc
		);
		ByteBuffer tailBuffer = getTailBuffer();
		setData(BufferUtil.connect(
				startBuffer,
				videoInfoBuffer,
				frameBuffer,
				alphaData,
				tailBuffer
		));
	}
	/**
	 * ref the frameBuffer
	 * @return
	 */
	private ByteBuffer getFrameBuffer() throws Exception {
		if(frameBuffer == null) {
			if(FlvCodecType.getVideoCodecType(codecId.get()) == FlvCodecType.H264) {
				if(frame instanceof SliceFrame) {
					frameBuffer = ((SliceFrame) frame).getDataPackBuffer();
				}
				else {
					// can be h264 multiframe(in the case of sei + sliceIDR.)
					throw new Exception("unexpected frame for h264.:" + frame);
				}
			}
			else {
				if(frame instanceof VideoMultiFrame) {
					throw new Exception("unexpected multiframe.:" + FlvCodecType.getVideoCodecType(codecId.get()));
				}
				else {
					frameBuffer = frame.getData();
				}
			}
		}
		return frameBuffer.duplicate();
	}
	/**
	 * analyze frame.0
	 * @throws Exception
	 */
	private void analyzeFrame() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameBuffer is undefined.");
		}
		if(getCodec() == FlvCodecType.H264 && packetType.get() != 1) {
			// in the case of h264 msh or h264 endOfSequence. no frame.
			return;
		}
		ByteBuffer buffer = frameBuffer;
		if(frameAnalyzer == null) {
			throw new Exception("frameAnalyzer is unknown.");
		}
		IReadChannel channel = new ByteReadChannel(buffer);
		// for video, container doesn't have information of width, height.
		do {
			IFrame analyzedFrame = frameAnalyzer.analyze(channel);
			if(analyzedFrame instanceof NullFrame) {
				continue;
			}
			VideoFrame videoFrame = (VideoFrame)analyzedFrame;
			videoFrame.setPts(getPts());
			videoFrame.setTimebase(getTimebase());
			if(dts != null) {
				videoFrame.setDts(dts.get());
			}
			if(frame != null) {
				if(!(frame instanceof VideoMultiFrame)) {
					VideoMultiFrame multiFrame = new VideoMultiFrame();
					multiFrame.addFrame(frame);
					frame = multiFrame;
				}
				((VideoMultiFrame)frame).addFrame((IVideoFrame)videoFrame);
			}
			else {
				frame = (IVideoFrame)videoFrame;
			}
		} while(channel.size() != channel.position());
		// remainFrame from frameAnalyzer
		IFrame lastFrame = frameAnalyzer.getRemainFrame();
		if(lastFrame != null && !(lastFrame instanceof NullFrame)) {
			VideoFrame videoFrame = (VideoFrame)lastFrame;
			videoFrame.setPts(getPts());
			videoFrame.setTimebase(getTimebase());
			if(dts != null) {
				videoFrame.setDts(dts.get());
			}
			if(frame != null) {
				if(!(frame instanceof VideoMultiFrame)) {
					VideoMultiFrame multiFrame = new VideoMultiFrame();
					multiFrame.addFrame(frame);
					frame = multiFrame;
				}
				((VideoMultiFrame)frame).addFrame((IVideoFrame)videoFrame);
			}
			else {
				frame = (IVideoFrame)videoFrame;
			}
		}
	}
	/**
	 * ref the frame.
	 * @return
	 * @throws Exception
	 */
	public IVideoFrame getFrame() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame;
	}
	/**
	 * width
	 * @return
	 * @throws Exception
	 */
	public int getWidth() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame.getWidth();
	}
	/**
	 * height
	 * @return
	 * @throws Exception
	 */
	public int getHeight() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame.getHeight();
	}
	/**
	 * add the frame.
	 * @param frame
	 */
	public void addFrame(IVideoFrame tmpFrame) throws Exception {
		if(tmpFrame == null) {
			return;
		}
		if(!(tmpFrame instanceof IVideoFrame)) {
			throw new Exception("try to append non-videoFrame for videoTag.");
		}
		frameAppendFlag = true;
		if(frame == null) {
			frame = tmpFrame;
		}
		else if(frame instanceof VideoMultiFrame) {
			((VideoMultiFrame) frame).addFrame(tmpFrame);
		}
		else {
			VideoMultiFrame multiFrame = new VideoMultiFrame();
			multiFrame.addFrame(frame);
			multiFrame.addFrame(tmpFrame);
			frame = multiFrame;
		}
		// frameから各情報を復元しないとだめ
		// 時間情報
		// size情報
		// streamId(0固定)
		// tagデータ(frameType, codecId)
		// (vp6,vp6a,h264の場合の特殊データ)
		// frameデータ実体
		// tail size
		super.update();
	}
	/**
	 * check is msh?
	 * @return
	 */
	public boolean isSequenceHeader() {
		return getCodec() == FlvCodecType.H264 && packetType.get() == 0;
	}
	/**
	 * is key?
	 * @return
	 */
	public boolean isKeyFrame() {
		return frameType.get() == 1;
	}
	/**
	 * is disposableInner(flv1)
	 * @return
	 */
	public boolean isDisposableInner() {
		return frameType.get() == 3;
	}
	/**
	 * initialize as h264 msh.
	 * @param frame
	 * @param sps
	 * @param pps
	 * @throws Exception
	 */
	public void setH264MediaSequenceHeader(H264Frame frame, SequenceParameterSet sps, PictureParameterSet pps) throws Exception {
		codecId.set(FlvCodecType.getVideoCodecNum(FlvCodecType.H264));
		frameType.set(1); // keyFrame
		packetType = new Bit8(0);
		dts = new Bit24(0);
		ConfigData configData = new ConfigData();
		frameBuffer = configData.makeConfigData(sps, pps);
		setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
		setTimebase(1000);
		setSize(11 + 1 + 4 + frameBuffer.remaining() + 4);
		super.update();
	}
	@Override
	public void setPts(long pts) {
		if(frame != null && frame instanceof VideoFrame) {
			VideoFrame vFrame = (VideoFrame) frame;
			vFrame.setPts(pts * vFrame.getTimebase() / 1000);
		}
		super.setPts(pts);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("VideoTag:");
		data.append(" timestamp:").append(getPts());
		if(dts != null) {
			data.append(" dts:").append(dts.get());
		}
		data.append(" codec:").append(getCodec());
		try {
			int width = getWidth();
			int height = getHeight();
			data.append(" size:").append(width).append("x").append(height);
		}
		catch(Exception e) {
		}
		return data.toString();
	}
}
