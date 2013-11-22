package com.ttProject.transcode.xuggle.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.util.BufferUtil;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * aacFrameからIPacketを作る動作
 * @author taktod
 */
public class AacPacketizer implements IPacketizer {
	/** 最終AacFrame */
	private Aac lastAacFrame = null;
	/**
	 * データをあらかじめ判定しておきます。
	 */
	@Override
	public boolean check(Unit unit) {
		if(!(unit instanceof Aac)) {
			return true;
		}
		// 前のデータを比較したい。
		if(lastAacFrame == null) {
			return true;
		}
		Aac aacFrame = (Aac)unit;
		return (aacFrame.getProfile() == lastAacFrame.getProfile()
				&& aacFrame.getSampleRate() == lastAacFrame.getSampleRate()
				&& aacFrame.getChannelConfiguration() == lastAacFrame.getChannelConfiguration());
	}
	/**
	 * aacFrameからpacketを取り出します。
	 */
	@Override
	public IPacket getPacket(Unit unit, IPacket packet) throws Exception {
		if(!(unit instanceof Aac)) {
			return null;
		}
		if(packet == null) {
			packet = IPacket.make();
		}
		Aac aacFrame = (Aac) unit;
		ByteBuffer buffer = aacFrame.getBuffer();
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, BufferUtil.toByteArray(buffer), 0, size);
		packet.setData(bufData);
//		packet.setDts(tag.getTimestamp());
//		packet.setPts(tag.getTimestamp());
//		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		return packet;
	}
	@Override
	public IStreamCoder createDecoder() throws Exception {
		return null;
	}
}
