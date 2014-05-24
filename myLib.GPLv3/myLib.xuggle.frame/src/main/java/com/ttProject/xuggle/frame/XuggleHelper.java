/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frame;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * flameデータをxuggleのpacketに変換するプログラム
 * @author taktod
 */
public class XuggleHelper {
	/**
	 * frameからpacketをつくる動作
	 * @param frame
	 * @return
	 */
	public static List<IPacket> getPackets(IFrame frame, IPacket packet) throws Exception {
		List<IPacket> result = new ArrayList<IPacket>();
		// packetからbyteBufferを取り出してあとは適当に処理する。
		if(frame instanceof AudioMultiFrame || frame instanceof VideoMultiFrame) {
			throw new Exception("マルチフレームからは情報がとれないようになっています");
		}
		if(frame instanceof IAudioFrame) {
			result.add(getAudioPacket((IAudioFrame)frame, packet));
		}
		else if(frame instanceof IVideoFrame) {
			result.add(getVideoPacket((IVideoFrame)frame, packet));
		}
		// その他
		return result;
	}
	/**
	 * 各packetを取り出します。packetが指定されている場合はそのメモリーを使いまわします
	 * @param frame
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private static IPacket getAudioPacket(IAudioFrame frame, IPacket packet) throws Exception {
		return null;
	}
	/**
	 * 各packetを取り出します。packetが指定されている場合はそのメモリーを使いまわします
	 * @param frame
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private static IPacket getVideoPacket(IVideoFrame frame, IPacket packet) throws Exception {
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer buffer = frame.getPackBuffer();
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setFlags(0);
		packet.setDts(frame.getDts());
		packet.setPts(frame.getPts());
		packet.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
		packet.setComplete(true, size);
		packet.setKeyPacket(frame.isKeyFrame());
		return packet;
	}
	/**
	 * frameから対象デコーダーを取得する動作
	 * @param frame
	 * @return
	 */
	public static IStreamCoder getDecoder(IFrame frame, IStreamCoder decoder) {
		if(frame instanceof Flv1Frame) {
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_FLV1) {
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_FLV1);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
		}
		return decoder;
	}
	/**
	 * packetからフレームをつくる動作
	 * @param packet
	 * @return
	 */
	public static IFrame getFrame(IPacket packet) {
		// TODO これをつくるにはIStreamCoderが必要。(どのコーデックかは、packetからはわからない。)
		return null;
	}
}
