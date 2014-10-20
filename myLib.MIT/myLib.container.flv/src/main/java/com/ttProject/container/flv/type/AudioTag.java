/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.FlvCodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.aac.AacDsiFrameAnalyzer;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * audiotag
 * @author taktod
 * nellymoser 16 or 8, sampleRate 0 bitCount 1 channels 0
 * speex 16Khz       , sampleRate 1 bitCount 1 channels 0
 */
public class AudioTag extends FlvTag {
	/** logger */
	private Logger logger = Logger.getLogger(AudioTag.class);
	private Bit4 codecId            = new Bit4();
	private Bit2 sampleRate         = new Bit2();
	private Bit1 bitCount           = new Bit1();
	private Bit1 channels           = new Bit1();
	private Bit8 sequenceHeaderFlag = null;
	
	private ByteBuffer    frameBuffer   = null;
	private IAudioFrame   frame         = null;
	private AudioAnalyzer frameAnalyzer = null;
	private boolean       frameAppendFlag = false; // フレームが追加されたことを検知するフラグ
	/**
	 * constructor
	 * @param tagType
	 */
	public AudioTag(Bit8 tagType) {
		super(tagType);
	}
	/**
	 * constructor
	 */
	public AudioTag() {
		this(new Bit8(0x08));
	}
	/**
	 * hold the analyzer object.
	 * @param analyzer
	 */
	public void setFrameAnalyzer(AudioAnalyzer analyzer) {
		this.frameAnalyzer = analyzer;
	}
	/**
	 * ref sampleRate.
	 * @return
	 * @throws Exception
	 */
	public int getSampleRate() throws Exception {
		if(frame == null) {
			switch(getCodec()) {
			case NELLY_16:
			case SPEEX:
				return 16000;
			case NELLY_8:
			case MP3_8:
			case G711_A:
			case G711_U:
				return 8000;
			default:
				switch(sampleRate.get()) {
				case 0:
					return 5512;
				case 1:
					return 11025;
				case 2:
					return 22050;
				case 3:
					return 44100;
				default:
					throw new Exception("unexpected sampleRate val:" + sampleRate.get());
				}
			}
		}
		return frame.getSampleRate();
	}
	/**
	 * the data for speex.(I cannot find any information about this. however, flv file works.)
	 * @return
	 */
	public int getSpeexFramesPerPacket() throws Exception {
		// TODO fix this.
		FlvCodecType codec = getCodec();
		if(codec != FlvCodecType.SPEEX) {
			throw new Exception("try to get the speex extra data for the codec " + codec);
		}
		switch(sampleRate.get()) {
		case 0:
			return 2;
		case 1:
			return sampleRate.get();
		default:
			throw new RuntimeException("unknown value for speex frames per packet information on audioTag.:" + sampleRate.get());
		}
	}
	/**
	 * ref sampleNum(depends on frame.)
	 * @return
	 * @throws Exception
	 */
	public int getSampleNum() throws Exception {
		if(frame == null) {
			analyzeFrame(); // analyze
		}
		return frame.getSampleNum();
	}
	/**
	 * ref channels
	 * @return
	 */
	public int getChannels() {
		if(frame == null) {
			if(channels.get() == 1) {
				return 2;
			}
			else {
				return 1;
			}
		}
		return frame.getChannel();
	}
	/**
	 * bitCount
	 * @return
	 */
	public int getBitCount() {
		if(frame == null) {
			if(bitCount.get() == 1) {
				return 16;
			}
			else {
				return 8;
			}
		}
		return frame.getBit();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(codecId != null) {
			switch(getCodec()) {
			case AAC:
				channel.position(getPosition() + 13);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 13 - 4);
				if(sequenceHeaderFlag.get() == 0) {
					if(frameAnalyzer == null || !(frameAnalyzer instanceof AacDsiFrameAnalyzer)) {
						throw new Exception("frameAnalyzer is not suitable for aac.");
					}
					frameAnalyzer.setPrivateData(new ByteReadChannel(frameBuffer));
				}
				break;
			default:
				if(getSize() - 12 - 4 > 0) {
					channel.position(getPosition() + 12);
					frameBuffer = BufferUtil.safeRead(channel, getSize() - 12 - 4);
				}
				else {
					channel.position(getPosition() + 11);
				}
				break;
			}
		}
		// check prevTagSize
		if(getPrevTagSize() != BufferUtil.safeRead(channel, 4).getInt()) {
			throw new Exception("the size of end tag is corrupted.");
		}
	}
	/**
	 * ref Codec
	 * @return
	 */
	public FlvCodecType getCodec() {
		return FlvCodecType.getAudioCodecType(codecId.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		if(getSize() == 15) {
			logger.warn("empty data is captured.");
			return;
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(codecId, sampleRate, bitCount, channels);
		if(getCodec() == FlvCodecType.AAC) {
			sequenceHeaderFlag = new Bit8();
			loader.load(sequenceHeaderFlag);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null && frame == null) {
			throw new Exception("data body is undefined.");
		}
		ByteBuffer frameBuffer = null;
		if(frameAppendFlag) {
			// TODO this frame analyze is other task, so it could be other func.
			// codecId, sampleRate, bitCount, channels will be analyzed from frame.
			IAudioFrame codecCheckFrame = frame;
			if(frame instanceof AudioMultiFrame) {
				// for codec check, need to ref first frame.
				codecCheckFrame = ((AudioMultiFrame) frame).getFrameList().get(0);
			}
			sampleRate   = null;
			bitCount     = null;
			channels     = null;
			int sizeEx = 0;
			switch(codecCheckFrame.getCodecType()) {
			case AAC:
				codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.AAC));
				sequenceHeaderFlag = new Bit8(1);
				sizeEx = 1;
				break;
			case MP3:
				if(frame.getSampleRate() == 8000) {
					// no example for mp3_8, I need a sample.
					codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.MP3_8));
					sampleRate = new Bit2();
				}
				else {
					codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.MP3));
				}
				break;
			case NELLYMOSER:
				if(frame.getSampleRate() == 16000) {
					// nelly16 0x42
					codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.NELLY_16));
					sampleRate = new Bit2();
				}
				else if(frame.getSampleRate() == 8000) {
					// nelly8 0x52
					codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.NELLY_8));
					sampleRate = new Bit2();
				}
				else {
					codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.NELLY));
				}
				break;
			case SPEEX:
				// 0xB6
				codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.SPEEX));
				if(frame.getSampleRate() != 16000) {
					throw new Exception("sampleRate of speex is 16kHz only.");
				}
				if(frame.getChannel() != 1) {
					throw new Exception("channel of speex is monoral only.");
				}
				sampleRate = new Bit2(1);
				break;
			case ADPCM_SWF:
				codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.ADPCM));
				break;
			case PCM_ALAW:
				codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.G711_A));
				sampleRate = new Bit2(0);
				break;
			case PCM_MULAW:
				codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.G711_U));
				sampleRate = new Bit2(0);
				break;
			default:
				throw new Exception("frame type is invalid for flv.:" + frame);
			}
			// channels
			if(channels == null) {
				channels = new Bit1();
				switch(frame.getChannel()) {
				case 1:
					channels.set(0);
					break;
				case 2:
					channels.set(1);
					break;
				default:
					throw new Exception("audio channel is not suitable for flv.:" + frame.getChannel());
				}
			}
			// bitCount
			if(bitCount == null) {
				bitCount = new Bit1();
				switch(frame.getBit()) {
				case 8:
					bitCount.set(0);
					break;
				case 16:
					bitCount.set(1);
					break;
				default:
					// some frame doesn't have bit depth information.
					bitCount.set(1);
				}
			}
			// sampleRate
			if(sampleRate == null) {
				sampleRate = new Bit2();
				switch((int)(frame.getSampleRate() / 100)) {
				case 55:
					sampleRate.set(0);
					break;
				case 110:
					sampleRate.set(1);
					break;
				case 220:
					sampleRate.set(2);
					break;
				case 441:
					sampleRate.set(3);
					break;
				default:
					throw new Exception("sampleRate is not suitable for flv.");
				}
			}
			frameAppendFlag = false;
			// reorganize data, do on getFrameBuffer
			// need to check size.
			frameBuffer = getFrameBuffer();
			setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
			setTimebase(1000);
			setSize(11 + 1 + sizeEx + frameBuffer.remaining() + 4);
		}
		else {
			frameBuffer = getFrameBuffer();
		}
		BitConnector connector = new BitConnector();
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer audioInfoBuffer = connector.connect(
				codecId, sampleRate, bitCount, channels,
				sequenceHeaderFlag /* extra data for aac */
		);
		ByteBuffer tailBuffer = getTailBuffer();
		setData(BufferUtil.connect(
				startBuffer,
				audioInfoBuffer,
				frameBuffer,
				tailBuffer
		));
	}
	/**
	 * ref frameBuffer
	 * @return
	 */
	private ByteBuffer getFrameBuffer() throws Exception {
		if(frameBuffer == null) {
			// make frame buffer from frame.
			if(frame != null) {
				// TODO for nellymoser this frame can be audioMultiFrame, in this case need to connect.
				if(frame instanceof AudioMultiFrame) {
					List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
					for(IAudioFrame aFrame : ((AudioMultiFrame) frame).getFrameList()) {
						buffers.add(aFrame.getData());
					}
					frameBuffer = BufferUtil.connect(buffers);
				}
				else if(frame instanceof AacFrame) {
					// for aac, get the buffer only. header is from msh.
					AacFrame aacFrame = (AacFrame)frame;
					frameBuffer = aacFrame.getBuffer();
				}
				else {
					frameBuffer = frame.getData();
				}
			}
		}
		if(frameBuffer == null) {
			return null;
		}
		else {
			return frameBuffer.duplicate();
		}
	}
	/**
	 * analyzeFrame
	 * @throws Exception
	 */
	private void analyzeFrame() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("target frame buffer is not loaded yet.");
		}
		if(getCodec() == FlvCodecType.AAC && sequenceHeaderFlag.get() != 1) {
			// aacのmshも処理しません。
			return;
		}
		if(frameAnalyzer == null) {
			throw new Exception("frameAnalyzer is unknown.");
		}
		IReadChannel channel = new ByteReadChannel(frameBuffer);
		AudioSelector selector = frameAnalyzer.getSelector();
		selector.setBit(getBitCount());
		selector.setChannel(getChannels());
//		selector.setSampleNum(getSampleNum()); // getSampleNum cause infinite loop.
		selector.setSampleRate(getSampleRate());
		double pts = getPts();
		do {
			AudioFrame audioFrame = (AudioFrame)frameAnalyzer.analyze(channel);
			audioFrame.setPts((long)pts);
			audioFrame.setTimebase(getTimebase());
			// need to get more collect data from sampleNum.
			pts += 1.0D * audioFrame.getSampleNum() * getTimebase() / audioFrame.getSampleRate();
			if(frame != null) {
				if(!(frame instanceof AudioMultiFrame)) {
					AudioMultiFrame multiFrame = new AudioMultiFrame();
					multiFrame.addFrame(frame);
					frame = multiFrame;
				}
				((AudioMultiFrame)frame).addFrame((IAudioFrame)audioFrame);
			}
			else {
				frame = (IAudioFrame)audioFrame;
			}
		} while(channel.size() != channel.position());
	}
	/**
	 * ref frame.
	 * @return
	 * @throws Exception
	 */
	public IAudioFrame getFrame() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame;
	}
	/**
	 * add frame.
	 * @param frame
	 */
	public void addFrame(IAudioFrame tmpFrame) throws Exception {
		if(tmpFrame == null) {
			// addedFrame is null, nothing.
			return;
		}
		if(!(tmpFrame instanceof IAudioFrame)) {
			throw new Exception("try to add non-audioFrame for audioTag.");
		}
		frameAppendFlag = true;
		if(frame == null) {
			frame = tmpFrame;
		}
		else if(frame instanceof AudioMultiFrame) {
			((AudioMultiFrame)frame).addFrame(tmpFrame);
		}
		else {
			AudioMultiFrame multiFrame = new AudioMultiFrame();
			multiFrame.addFrame(frame);
			if(tmpFrame instanceof AudioMultiFrame) {
				for(IAudioFrame aFrame : ((AudioMultiFrame) tmpFrame).getFrameList()) {
					multiFrame.addFrame(aFrame);
				}
			}
			else {
				multiFrame.addFrame(tmpFrame);
			}
			frame = multiFrame;
		}
		super.update();
	}
	/**
	 * check msh.
	 * @return
	 */
	public boolean isSequenceHeader() {
		return getCodec() == FlvCodecType.AAC && sequenceHeaderFlag.get() == 0;
	}
	/**
	 * initialize as aac msh.
	 * @param dsi
	 */
	public void setAacMediaSequenceHeader(AacFrame frame, ByteBuffer data) throws Exception {
		codecId.set(FlvCodecType.getAudioCodecNum(FlvCodecType.AAC));
		switch(frame.getChannel()) {
		case 1:
			channels.set(0);
			break;
		case 2:
			channels.set(1);
			break;
		default:
			throw new Exception("channel is not suitable for audioTag.:" + frame.getChannel());
		}
		switch(frame.getBit()) {
		case 8:
			bitCount.set(0);
			break;
		case 16:
		default:
			bitCount.set(1);
			break;
		}
		switch((int)(frame.getSampleRate() / 100)) {
		case 55:
			sampleRate.set(0);
			break;
		case 110:
			sampleRate.set(1);
			break;
		case 220:
			sampleRate.set(2);
			break;
		case 441:
			sampleRate.set(3);
			break;
		default:
			throw new Exception("frameRate is not suitable for flv.:" + frame.getSampleRate());
		}
		sequenceHeaderFlag = new Bit8(0);
		frameBuffer = data;
		// calcurate pts.
		setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
		setTimebase(1000);
		setSize(11 + 1 + 1 + frameBuffer.remaining() + 4);
		super.update();
	}
	@Override
	public void setPts(long pts) {
		// update frame pts before container pts.
		if(frame != null && frame instanceof AudioFrame) {
			AudioFrame aFrame = (AudioFrame)frame;
			aFrame.setPts(pts * aFrame.getTimebase() / 1000);
		}
		super.setPts(pts);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("AudioTag:");
		data.append(" timestamp:").append(getPts());
		data.append(" codec:").append(getCodec());
		try {
			int sampleRate = getSampleRate();
			data.append(" sampleRate:").append(sampleRate);
			int sampleNum = getSampleNum();
			data.append(" sampleNum:").append(sampleNum);
		}
		catch(Exception e) {
		}
		return data.toString();
	}
}
