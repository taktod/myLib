package com.ttProject.transcode.xuggle.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.media.mp3.frame.Mp3;
import com.ttProject.util.BufferUtil;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * mp3FrameからIPacketを作る動作
 * @author taktod
 *
 */
public class Mp3Packetizer implements IPacketizer {
	/** 最終mp3Frame */
	private Mp3 lastMp3Frame = null;
	/**
	 * データをあらかじめ判定しておきます。
	 */
	@Override
	public boolean check(Unit unit) {
		if(!(unit instanceof Mp3)) {
			return true;
		}
		if(lastMp3Frame == null) {
			return true;
		}
		Mp3 mp3Frame = (Mp3)unit;
		return (mp3Frame.getBitrate() == lastMp3Frame.getBitrate()
				&& mp3Frame.getSampleRate() == lastMp3Frame.getSampleRate()
				&& mp3Frame.getChannelMode() == lastMp3Frame.getChannelMode());
	}
	/**
	 * mp3Frameからpacketを取り出します。
	 */
	@Override
	public IPacket getPacket(Unit unit, IPacket packet) throws Exception {
		if(!(unit instanceof Mp3)) {
			return null;
		}
		if(packet == null) {
			packet = IPacket.make();
		}
		Mp3 mp3Frame = (Mp3)unit;
		ByteBuffer buffer = mp3Frame.getBuffer();
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, BufferUtil.toByteArray(buffer), 0, size);
		packet.setData(bufData);
//		packet.setDts(tag.getTimestamp());
//		packet.setPts(tag.getTimestamp());
//		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		return packet;
	}
	/**
	 * decoderを応答します。
	 */
	@Override
	public IStreamCoder createDecoder() throws Exception {
		if(lastMp3Frame == null) {
			return null;
		}
		IStreamCoder decoder = null;
		decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_MP3);
		decoder.setSampleRate(lastMp3Frame.getSampleRate());
		decoder.setTimeBase(IRational.make(1, lastMp3Frame.getSampleRate()));
		decoder.setChannels(lastMp3Frame.getChannels());
		return decoder;
	}
}
