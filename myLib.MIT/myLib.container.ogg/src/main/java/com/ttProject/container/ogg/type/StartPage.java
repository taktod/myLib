/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.ttProject.container.ogg.Crc32;
import com.ttProject.container.ogg.OggPage;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.opus.OpusFrameAnalyzer;
import com.ttProject.frame.speex.SpeexFrameAnalyzer;
import com.ttProject.frame.theora.TheoraFrameAnalyzer;
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * startPage(will have header data.)
 * @author taktod
 * 
 * TODO Out of Memoryが発生する可能性があるので、frameListをpageごとに保持するように変更したほうがよい。
 * crc32の計算が微妙・・・どうすりゃいいんだ。
 */
public class StartPage extends OggPage {
	/** logger */
	private Logger logger = Logger.getLogger(StartPage.class);
	/** frame Analyzer */
	private IAnalyzer analyzer = null;
	/** passedTic(for audio only.) */
	private long passedTic = 0;
	/**
	 * constructor
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public StartPage(Bit8 version,
			Bit1 packetContinurousFlag,
			Bit1 logicStartFlag,
			Bit1 logicEndFlag,
			Bit5 zeroFill) {
		super(version, packetContinurousFlag, logicStartFlag, logicEndFlag, zeroFill);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		boolean isFirstData = true;
		channel.position(getPosition() + 27 + getSegmentSizeList().size());
		int targetSize = 0;
		for(Bit8 size : getSegmentSizeList()) {
			if(size.get() == 0xFF) {
				targetSize += 0xFF;
			}
			targetSize += size.get();
			ByteBuffer buffer = BufferUtil.safeRead(channel, targetSize);
			if(isFirstData) {
				// firstData will have codecInformation on header.
				switch(buffer.get()) {
				case 0x01:
					logger.info("vorbis?");
					analyzer = new VorbisFrameAnalyzer();
					break;
				case 'S':
					logger.info("speex?");
					analyzer = new SpeexFrameAnalyzer();
					break;
				case (byte)0x80:
					logger.info("theora?");
					analyzer = new TheoraFrameAnalyzer();
					break;
				case 'O':
					logger.info("opus?");
					analyzer = new OpusFrameAnalyzer();
					break;
				default:
					throw new Exception("unknown codec is found.");
				}
				buffer.position(0);
			}
			isFirstData = false;
			IReadChannel bufferChannel = new ByteReadChannel(buffer);
			getFrameList().add((IFrame)analyzer.analyze(bufferChannel));
			// need to read all of bufferChannel
		}
		channel.position(getPosition() + getSize());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// here is the position to make buffer.
		// write header buffer.
		ByteBuffer headerBuffer = getHeaderBuffer();
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(headerBuffer);
		// write frame
		for(IFrame frame : getFrameList()) {
			buffer.put(frame.getData());
		}
		ByteBuffer tmpBuffer = buffer.duplicate();
		tmpBuffer.flip();
		// make crc32
		Crc32 crc32 = new Crc32();
		while(tmpBuffer.remaining() > 0) {
			crc32.update(tmpBuffer.get());
		}
		// write crc32
		buffer.position(22);
		buffer.putInt((int)crc32.getValue());
		buffer.position(tmpBuffer.position());
		buffer.flip();
		setData(buffer);
	}
	/**
	 * ref the frame analyzer
	 * @return
	 */
	public IAnalyzer getAnalyzer() {
		return analyzer;
	}
	/**
	 * set the passedTic(sampleNum)
	 * @param passedTic
	 */
	public void setPassedTic(long passedTic) {
		this.passedTic = passedTic;
	}
	/**
	 * ref the passedTic(sampleNum)
	 * @return
	 */
	public long getPassedTic() {
		return passedTic;
	}
}
