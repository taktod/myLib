/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * analyzer for h264 nal.
 * ex:mpegts 00 00 01 64 ...... 
 * @author taktod
 */
public class NalAnalyzer extends H264FrameAnalyzer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(NalAnalyzer.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		Short lastData = null;
		ByteBuffer buffer = ByteBuffer.allocate(channel.size() - channel.position());
		// load data.
		while(channel.size() - channel.position() > 1) {
			short data = BufferUtil.safeRead(channel, 2).getShort();
			// 00 00 00 01 or 00 00 01 is the sign for nal.
			// so check deeply in the case of 00 00(short == 0)
			if(data == 0) {
				byte firstByte, secondByte;
				firstByte = BufferUtil.safeRead(channel, 1).get();
				if(firstByte == 1) {
					checkLastData(buffer, lastData);
					buffer.flip();
					if(buffer.remaining() == 0) {
						buffer = ByteBuffer.allocate(channel.size() - channel.position());
						continue;
					}
					return setupFrame(buffer);
				}
				else if(firstByte == 0) {
					secondByte = BufferUtil.safeRead(channel, 1).get();
					if(secondByte == 1) {
						checkLastData(buffer, lastData);
						buffer.flip();
						if(buffer.remaining() == 0) {
							buffer = ByteBuffer.allocate(channel.size() - channel.position());
							continue;
						}
						return setupFrame(buffer);
					}
					else {
						if(lastData != null) {
							buffer.putShort(lastData);
						}
						buffer.putShort(data);
						buffer.put(firstByte);
						buffer.put(secondByte);
					}
				}
				else {
					if(lastData != null) {
						buffer.putShort(lastData);
					}
					buffer.putShort(data);
					buffer.put(firstByte);
				}
				lastData = null;
			}
			else { // not 0(cannot be sign of nal.)
				if(lastData != null && data == 1 && (lastData & 0x00FF) == 0) {
					checkLastData(buffer, lastData);
					buffer.flip();
					if(buffer.remaining() == 0) {
						buffer = ByteBuffer.allocate(channel.size() - channel.position());
						continue;
					}
					return setupFrame(buffer);
				}
				setLastData(buffer, lastData);
				lastData = data;
			}
		}
		setLastData(buffer, lastData);
		if(channel.size() - channel.position() == 1) {
			buffer.put(BufferUtil.safeRead(channel, 1).get());
		}
		buffer.flip();
		if(buffer.remaining() == 0) {
			return null;
		}
		return setupFrame(buffer);
	}
	/**
	 * set the last data of nal.
	 * @param buffer
	 * @param lastData
	 */
	private void setLastData(ByteBuffer buffer, Short lastData) {
		if(lastData != null) {
			buffer.putShort(lastData);
		}
	}
	/**
	 * check the last data of nal.
	 * @param buffer
	 * @param lastData
	 */
	private void checkLastData(ByteBuffer buffer, Short lastData) {
		if(lastData != null) {
			if((lastData & 0x00FF) == 0) {
				buffer.put((byte)(lastData >>> 8));
			}
			else {
				buffer.putShort(lastData);
			}
		}
	}
}
