/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU AFFERO GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.humble.frameutil;

import java.nio.ByteBuffer;

import io.humble.ferry.Buffer;
import io.humble.video.Codec;
import io.humble.video.Decoder;
import io.humble.video.MediaPacket;
import io.humble.video.Rational;

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

/**
 * frame -> packet変換
 * とりあえず、xuggleのときみたいに、Bufferを使い回して・・・というやり方が有効かわからないので、毎回つくる動作からやってみます。
 * @author taktod
 */
public class Packetizer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Packetizer.class);
	/**
	 * packetをframeから取り出します()
	 * @param frame
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	public MediaPacket getPacket(IFrame frame, MediaPacket packet) throws Exception {
//		if(packet == null) {
//			packet = MediaPacket.make();
//		}
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
	private MediaPacket getVideoPacket(IVideoFrame frame, MediaPacket packet) throws Exception {
		ByteBuffer buffer = frame.getPackBuffer();
		if(buffer == null) {
			return null;
		}
		int size = buffer.remaining();
		Buffer bufData = Buffer.make(null, buffer.array(), 0, size);
		packet = MediaPacket.make(bufData);
		packet.setFlags(0);
		packet.setDts(frame.getDts());
		packet.setPts(frame.getPts());
		packet.setTimeBase(Rational.make(1, (int)frame.getTimebase()));
		packet.setKeyPacket(frame.isKeyFrame());
		// completeの設定がないみたい・・・どうなるんだ？(勝手にcompleteはいるっぽいです。)
		return packet;
	}
	/**
	 * 音声パケットを作成する
	 * @param frame
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private MediaPacket getAudioPacket(IAudioFrame frame, MediaPacket packet) throws Exception {
		ByteBuffer buffer = frame.getPackBuffer();
		if(buffer == null) {
			return null;
		}
		int size = buffer.remaining();
		Buffer bufData = Buffer.make(null, buffer.array(), 0, size);
		packet = MediaPacket.make(bufData);
		packet.setPts(frame.getPts());
		packet.setTimeBase(Rational.make(1, (int)frame.getTimebase()));
		// こっちもコンプリートフラグがない・・・(勝手にcompleteはいるみたいですね。)
		return packet;
	}
	/**
	 * フレームに対応するデコーダーを応答する
	 * @param frame
	 * @param decoder
	 * @return
	 * @throws Exception
	 */
	public Decoder getDecoder(IFrame frame, Decoder decoder) throws Exception {
		switch(frame.getCodecType()) {
		case AAC:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_AAC) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_AAC);
				decoder.open(null, null);
			}
			break;
		case ADPCM_IMA_WAV:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_ADPCM_IMA_WAV) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_ADPCM_IMA_WAV);
				decoder.open(null, null);
			}
			break;
		case ADPCM_SWF:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_ADPCM_SWF) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_ADPCM_SWF);
				decoder.open(null, null);
			}
			break;
		case MP3:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_MP3) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_MP3);
				decoder.open(null, null);
			}
			break;
		case NELLYMOSER:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_NELLYMOSER) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_NELLYMOSER);
				decoder.open(null, null);
			}
			break;
		case OPUS:
			// opusは多分codecPrivateがあると思う。
		case PCM_ALAW:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_PCM_ALAW) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_PCM_ALAW);
				decoder.open(null, null);
			}
			break;
		case PCM_MULAW:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_PCM_MULAW) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_PCM_MULAW);
				decoder.open(null, null);
			}
			break;
		case SPEEX:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_SPEEX) {
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_SPEEX);
				decoder.open(null, null);
			}
			break;
		case VORBIS:
			if(decoder == null || decoder.getCodecID() != Codec.ID.CODEC_ID_VORBIS) {
				if(frame instanceof IdentificationHeaderFrame || frame instanceof CommentHeaderFrame || frame instanceof SetupHeaderFrame) {
					// 初期化中のデータの場合は処理できない。
					return null;
				}
				decoder = makeAudioDecoder((IAudioFrame)frame, Codec.ID.CODEC_ID_VORBIS);
				VorbisFrame vorbisFrame = (VorbisFrame)frame;
				// ここでextraDataをつけておきます。
				ByteBuffer buffer = vorbisFrame.getPrivateData();
				int size = buffer.remaining();
				Buffer extraData = Buffer.make(decoder, buffer.array(), 0, size);
				// どうやってextraデータいれるんだ？これ・・・
			}
			
		case FLV1:
		case H264:
		case MJPEG:
		case THEORA:
		case VP6:
		case VP8:
		case VP9:
			
		case H265:
		case NONE:
		default:
			throw new RuntimeException("humble video doesn't support these codec:" + frame.getCodecType());
		}
		return decoder;
	}
	private Decoder makeAudioDecoder(IAudioFrame frame, Codec.ID id) throws Exception {
		Decoder decoder = null;
		if(frame.getSampleRate() == 0 || frame.getTimebase() == 0 || frame.getChannel() == 0) {
			// audioFrameなのに定義がない場合は、作成できないので、処理しない(metaデータとか)
			return null;
		}
		Codec codec = Codec.findDecodingCodec(id);
		decoder = Decoder.make(codec);
		decoder.setSampleRate(frame.getSampleRate());
//		decoder.setTimeBase(Rational.make(1, (int)frame.getTimebase()));
		decoder.setChannels(frame.getChannel());
		// ここでopenしてしまうとvorbisはうまく動作しないみたい。
		return decoder;
	}
}
