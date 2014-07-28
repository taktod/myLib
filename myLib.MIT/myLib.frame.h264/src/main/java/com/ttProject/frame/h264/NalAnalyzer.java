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
 * mpegtsのような00 00 01 + dataのnalを解析する動作
 * 実体の読み込みまで実施します。
 * @author taktod
 */
public class NalAnalyzer extends H264FrameAnalyzer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(NalAnalyzer.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		Short lastData = null;
		ByteBuffer buffer = ByteBuffer.allocate(channel.size() - channel.position());
		// データを読み込んでいく
		while(channel.size() - channel.position() > 1) {
			short data = BufferUtil.safeRead(channel, 2).getShort();
			// 00 00 00 01もしくは 00 00 01がnalの分岐点
			// よってshort = 0になった場合に注意して処理すればいい
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
			else { // 0ではない
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
	 * 最終読み込み途上データを設定
	 * @param buffer
	 * @param lastData
	 */
	private void setLastData(ByteBuffer buffer, Short lastData) {
		if(lastData != null) {
			buffer.putShort(lastData);
		}
	}
	/**
	 * 最終読み込み途上データを確認
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
