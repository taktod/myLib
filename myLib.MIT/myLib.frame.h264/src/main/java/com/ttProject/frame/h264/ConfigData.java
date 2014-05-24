/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264;

import java.nio.ByteBuffer;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.util.BufferUtil;

/**
 * flvのmediaSequenceHeaderやmp4のdecodeBox(Avcc)の内容から、
 * nalデータを取り出す動作
 * 01 4D 40 1E FF E1 00 19 67 4D 40 1E 92 42 01 40 5F F2 E0 22 00 00 03 00 C8 00 00 2E D5 1E 2C 5C 90 01 00 04 68 EE 32 C8
 * [] avcC version 1
 *    [      ] profile compatibility level
 *             [] 111111 + 2bit nal size - 1(ff固定とおもっててOKでしょう)
 *                [] number of SPS e1固定？
 *                   [   ] spsLength
 *                         [spsNalデータ                                                             ]
 *                                                                                                    [] number of PPS
 *                                                                                                       [   ] ppsLength
 *                                                                                                             [         ] ppsData
 * もしくはnalデータからconfigDataを作り出す動作
 * @author taktod
 */
public class ConfigData {
	/** 動作セレクター(セレクターにsps ppsを保持させて、他のframeに持たせる必要があるので、ここで設置できるようにしてある) */
	private H264FrameSelector selector = null;
	/**
	 * セレクターの設定
	 * @param selector
	 */
	public void setSelector(H264FrameSelector selector) {
		this.selector = selector;
	}
	/**
	 * spsとppsを取り出す
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IVideoFrame getNalsFrame(IReadChannel channel) throws Exception {
		ISelector selector = null;
		if(this.selector != null) {
			selector = this.selector;
		}
		else {
			selector = new H264FrameSelector();
		}
		VideoMultiFrame result = new VideoMultiFrame();
		if(channel.size() - channel.position() < 8) {
			throw new Exception("先頭データの読み込み部のサイズが小さすぎます。");
		}
		ByteBuffer buffer = BufferUtil.safeRead(channel, 6);
		if(buffer.get() != 0x01) {
			throw new Exception("avccVersionが1でないみたいです。");
		}
		short spsSize = BufferUtil.safeRead(channel, 2).getShort();
		IReadChannel byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, spsSize));
		IVideoFrame sps = (IVideoFrame)selector.select(byteChannel);
		if(!(sps instanceof SequenceParameterSet)) {
			throw new Exception("取得データがspsではありませんでした。");
		}
		sps.load(byteChannel);
		result.addFrame(sps);
		BufferUtil.safeRead(channel, 1);
		short ppsSize = BufferUtil.safeRead(channel, 2).getShort();
		byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, ppsSize));
		IVideoFrame pps = (IVideoFrame)selector.select(byteChannel);
		if(!(pps instanceof PictureParameterSet)) {
			throw new Exception("取得データがppsではありませんでした。");
		}
		pps.load(byteChannel);
		result.addFrame(pps);
		return result;
	}
	/**
	 * configDataをspsとppsから作成します。
	 * @param sps
	 * @param pps
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer makeConfigData(SequenceParameterSet sps, PictureParameterSet pps) throws Exception {
		ByteBuffer spsBuffer = sps.getData();
		ByteBuffer ppsBuffer = pps.getData();
		ByteBuffer data = ByteBuffer.allocate(11 + spsBuffer.remaining() + ppsBuffer.remaining());
		data.put((byte)1);
		spsBuffer.position(1);
		data.put(spsBuffer.get());
		data.put(spsBuffer.get());
		data.put(spsBuffer.get());
		spsBuffer.position(0);
		data.put((byte)0xFF);
		data.put((byte)0xE1);
		data.putShort((short)spsBuffer.remaining());
		data.put(spsBuffer);
		data.put((byte)1);
		data.putShort((short)ppsBuffer.remaining());
		data.put(ppsBuffer);
		data.flip();
		return data;
	}
}
