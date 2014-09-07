/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frame;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.frame.vorbis.type.CommentHeaderFrame;
import com.ttProject.frame.vorbis.type.IdentificationHeaderFrame;
import com.ttProject.frame.vorbis.type.SetupHeaderFrame;
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
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Packetizer.class);
	/**
	 * packetをframeから取り出します(マルチフレームの場合に一気に応答したかったのですが、そうするとIPacketの使いまわしができないので１つのみの応答にします)
	 * @param frame
	 * @param packet
	 * @return データがとれない場合はnull データがとれる場合はIPacketオブジェクト
	 */
	public IPacket getPacket(IFrame frame, IPacket packet) throws Exception {
		if(packet == null) {
			packet = IPacket.make();
		}
		if(frame instanceof AudioMultiFrame) {
			throw new Exception("マルチフレームは未対応です");
		}
		else if(frame instanceof VideoMultiFrame) {
			throw new Exception("マルチフレームは未対応です");
		}
		else if(frame instanceof IAudioFrame) {
			return getAudioPacket((IAudioFrame)frame, packet);
		}
		else if(frame instanceof IVideoFrame) {
			return getVideoPacket((IVideoFrame)frame, packet);
		}
		return null;
	}
	/**
	 * 映像パケットを作成する
	 * @param frame
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private IPacket getVideoPacket(IVideoFrame frame, IPacket packet) throws Exception {
		ByteBuffer buffer = frame.getPackBuffer();
		if(buffer == null) {
			return null;
		}
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
	 * 音声パケットを作成する
	 * @param frame
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private IPacket getAudioPacket(IAudioFrame frame, IPacket packet) throws Exception {
		ByteBuffer buffer = frame.getPackBuffer();
		if(buffer == null) {
			return null;
		}
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setPts(frame.getPts());
		packet.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
		packet.setComplete(true, size);
		return packet;
	}
	/**
	 * フレームに対応するデコーダーを応答する
	 * @param frame
	 * @param decoder
	 * @return
	 */
	public IStreamCoder getDecoder(IFrame frame, IStreamCoder decoder) throws Exception {
		switch(frame.getCodecType()) {
		case AAC:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_AAC) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_AAC);
			}
			break;
		case ADPCM_IMA_WAV:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_ADPCM_IMA_WAV) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_ADPCM_IMA_WAV);
			}
			break;
		case ADPCM_SWF:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_ADPCM_SWF) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_ADPCM_SWF);
			}
			break;
		case MP3:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_MP3) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_MP3);
			}
			break;
		case NELLYMOSER:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_NELLYMOSER) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_NELLYMOSER);
			}
			break;
		case PCM_ALAW:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_PCM_ALAW) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_PCM_ALAW);
			}
			break;
		case PCM_MULAW:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_PCM_MULAW) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_PCM_MULAW);
			}
			break;
		case SPEEX:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_SPEEX) {
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_SPEEX);
			}
			break;
		case VORBIS:
			if(decoder == null|| decoder.getCodecID() != ICodec.ID.CODEC_ID_VORBIS) {
				if(frame instanceof IdentificationHeaderFrame || frame instanceof CommentHeaderFrame || frame instanceof SetupHeaderFrame) {
					// 初期化中のデータの場合は処理できない。
					return null;
				}
				decoder = makeAudioDecoder((IAudioFrame) frame, ICodec.ID.CODEC_ID_VORBIS);
				VorbisFrame vorbisFrame = (VorbisFrame)frame;
				// このタイミングでextraDataをいれないとだめっぽい
				ByteBuffer buffer = vorbisFrame.getPrivateData();
				int size = buffer.remaining();
				IBuffer extraData = IBuffer.make(decoder, buffer.array(), 0, size);
				decoder.setExtraData(extraData, 0, size, true);
			}
			break;

		case FLV1:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_FLV1) {
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_FLV1);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
			break;
		case H264:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_H264) {
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_H264);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
			break;
		case MJPEG:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_MJPEG) {
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_MJPEG);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
			break;
		case THEORA:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_THEORA) {
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_THEORA);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
			break;
		case VP6:
			if(decoder == null || decoder.getCodecID() != ICodec.ID.CODEC_ID_VP6F) {
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_VP6F);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
			break;
		case VP8:
			if(decoder == null|| decoder.getCodecID() != ICodec.ID.CODEC_ID_VP8) {
				decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_VP8);
				decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
			}
			break;
		case H265:
		case NONE:
		case OPUS:
		case VP9:
		default:
			throw new RuntimeException("xuggle doesn't support these codec:" + frame.getCodecType());
		}
		return decoder;
	}
	/**
	 * audioのデコーダーを作成する
	 * @param frame
	 * @param id
	 * @return
	 */
	private IStreamCoder makeAudioDecoder(IAudioFrame frame, ICodec.ID id) {
		IStreamCoder decoder = null;
		if(frame.getSampleRate() == 0 || frame.getTimebase() == 0 || frame.getChannel() == 0) {
			// audioFrameの定義情報がかけている場合は、処理の参考にならないframeなので、処理を飛ばす(metaデータとか)
			return null;
		}
		decoder = IStreamCoder.make(Direction.DECODING, id);
		decoder.setSampleRate(frame.getSampleRate());
		decoder.setTimeBase(IRational.make(1, (int)frame.getTimebase()));
		decoder.setChannels(frame.getChannel());
		return decoder;
	}
}
