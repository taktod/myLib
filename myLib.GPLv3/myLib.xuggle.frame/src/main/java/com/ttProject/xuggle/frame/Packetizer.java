package com.ttProject.xuggle.frame;

import java.nio.ByteBuffer;

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
 * frame -> packet変換
 * @author taktod
 *
 */
public class Packetizer {
	/**
	 * packetをframeから取り出します(マルチフレームの場合に一気に応答したかったのですが、そうするとIPacketの使いまわしができないので１つのみの応答にします)
	 * @param frame
	 * @param packet
	 * @return データがとれない場合はnull データがとれる場合はIPacketオブジェクト
	 */
	public IPacket getPacket(IFrame frame, IPacket packet) throws Exception {
		if(frame instanceof AudioMultiFrame) {
			throw new Exception("マルチフレームはまだ未対応です");
		}
		else if(frame instanceof VideoMultiFrame) {
			throw new Exception("マルチフレームはまだ未対応です");
		}
		else if(frame instanceof IAudioFrame) {
			return getAudioPacket((IAudioFrame)frame, packet);
		}
		else if(frame instanceof IVideoFrame) {
			return getVideoPacket((IVideoFrame)frame, packet);
		}
		
		return null;
	}
	private IPacket getVideoPacket(IVideoFrame frame, IPacket packet) throws Exception {
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
	private IPacket getAudioPacket(IAudioFrame frame, IPacket packet) throws Exception {
		return null;
	}
	/**
	 * フレームに対応するデコーダーを応答する
	 * @param frame
	 * @param decoder
	 * @return
	 */
	public IStreamCoder getDecoder(IFrame frame, IStreamCoder decoder) {
		if(frame instanceof Flv1Frame) {
			if(decoder == null // デコーダーが未設定の場合はつくる必要あり
					|| decoder.getCodecID() != ICodec.ID.CODEC_ID_FLV1) { // コーデックがflv1でない場合も作り直し
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_FLV1);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
		}
		return decoder;
	}
}
