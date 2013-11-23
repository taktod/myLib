package com.ttProject.transcode.xuggle.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.transcode.xuggle.exception.FormatChangeException;
import com.ttProject.util.BufferUtil;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * flvTagからIPacketを作る動作
 * @author taktod
 */
public class FlvAudioPacketizer implements IPacketizer {
	/** 最終音声タグ */
	private AudioTag lastAudioTag = null;
	/** dsiデータ */
	private DecoderSpecificInfo dsi = null;
	/**
	 * データをあらかじめ確認します。
	 */
	@Override
	public boolean check(Unit unit) throws FormatChangeException {
		// 先にデータを確認して、データの整合性を確認します。
		// なおtypeが違う場合は、そのままtrueを返します。
		// falseになる場合の例：中途でコーデックがかわったなど
		if(!(unit instanceof AudioTag)) {
			return false;
		}
		if(lastAudioTag == null) {
			// 始めのデータなので問題なし
			return true;
		}
		AudioTag aTag = (AudioTag) unit;
		// 一致してたらtrue 違ったらfalse
		if(aTag.getCodec() == lastAudioTag.getCodec()
				&& aTag.getChannels() == lastAudioTag.getChannels()
				&& aTag.getSampleRate() == lastAudioTag.getSampleRate()) {
			return true;
		}
		throw new FormatChangeException();
	}
	/**
	 * tagからpacketを取り出します。
	 */
	@Override
	public IPacket getPacket(Unit unit, IPacket packet) throws Exception {
		if(unit instanceof AudioTag) {
			lastAudioTag = (AudioTag) unit;
			switch(lastAudioTag.getCodec()) {
			case AAC:
				return getAACPacket(lastAudioTag, packet);
			case MP3_8:
			case MP3:
				dsi = null;
				return getMp3Packet(lastAudioTag, packet);
			case NELLY_16:
			case NELLY_8:
			case NELLY:
				dsi = null;
				return getNellyPacket(lastAudioTag, packet);
			case SPEEX:
				dsi = null;
				return getSpeexPacket(lastAudioTag, packet);
			case PCM:
			case ADPCM:
			case G711_A:
			case G711_U:
			case RESERVED:
			case DEVICE_SPECIFIC:
				dsi = null;
				throw new RuntimeException(lastAudioTag.getCodec() + "の変換は未実装です。");
			default:
				break;
			}
		}
		return null;
	}
	/**
	 * mp3用のIPacketを生成します
	 * @param tag
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private IPacket getMp3Packet(AudioTag tag, IPacket packet) throws Exception {
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer rawData = tag.getRawData();
		int size = rawData.remaining();
		IBuffer bufData = IBuffer.make(null, rawData.array(), 0, size);
		packet.setData(bufData);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		return packet;
	}
	/**
	 * nellymoser用のIPacketを生成します
	 * @param tag
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private IPacket getNellyPacket(AudioTag tag, IPacket packet) throws Exception {
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer rawData = tag.getRawData();
		int size = rawData.remaining();
		IBuffer bufData = IBuffer.make(null, rawData.array(), 0, size);
		packet.setData(bufData);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		return packet;
	}
	/**
	 * speex用のIPacketを生成します
	 * @param tag
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private IPacket getSpeexPacket(AudioTag tag, IPacket packet) throws Exception {
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer rawData = tag.getRawData();
		int size = rawData.remaining();
		IBuffer bufData = IBuffer.make(null, rawData.array(), 0, size);
		packet.setData(bufData);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		return packet;
	}
	/**
	 * aac用のIPacketを生成します。
	 * @param tag
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private IPacket getAACPacket(AudioTag tag, IPacket packet) throws Exception {
		if(tag.isMediaSequenceHeader()) {
			dsi = new DecoderSpecificInfo();
			dsi.analyze(new ByteReadChannel(tag.getRawData()));
			return null;
		}
		if(dsi == null) {
			throw new RuntimeException("decoderSpecificInfoが決定していません");
		}
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer rawData = tag.getRawData();
		int size = rawData.remaining();
		Aac aac = new Aac(size, dsi);
		aac.setData(rawData);
		ByteBuffer buffer = aac.getBuffer();
		size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, BufferUtil.toByteArray(buffer), 0, size);
		packet.setData(bufData);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		return packet;
	}
	/**
	 * 現在動作しているデータに対応するデコーダーを取得します
	 */
	@Override
	public IStreamCoder createDecoder() throws Exception {
		if(lastAudioTag == null) {
			return null;
		}
		IStreamCoder decoder = null;
		switch(lastAudioTag.getCodec()) {
		case AAC: // win64bit版のxuggleのaacデコードにはバグがあるみたいです。xuggle-5.4のコンパイルがどうやら32bitになっている感じ？
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_AAC);
			decoder.setSampleRate(lastAudioTag.getSampleRate());
			decoder.setTimeBase(IRational.make(1, lastAudioTag.getSampleRate()));
			decoder.setChannels(lastAudioTag.getChannels());
			break;
		case MP3:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_MP3);
			decoder.setSampleRate(lastAudioTag.getSampleRate());
			decoder.setTimeBase(IRational.make(1, lastAudioTag.getSampleRate()));
			decoder.setChannels(lastAudioTag.getChannels());
			break;
		case MP3_8:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_MP3);
			decoder.setSampleRate(8000);
			decoder.setTimeBase(IRational.make(1, lastAudioTag.getSampleRate()));
			decoder.setChannels(lastAudioTag.getChannels());
			break;
		case NELLY:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_NELLYMOSER);
			decoder.setSampleRate(lastAudioTag.getSampleRate());
			decoder.setTimeBase(IRational.make(1, lastAudioTag.getSampleRate()));
			decoder.setChannels(lastAudioTag.getChannels());
			break;
		case NELLY_8:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_NELLYMOSER);
			decoder.setSampleRate(8000);
			decoder.setTimeBase(IRational.make(1, lastAudioTag.getSampleRate()));
			decoder.setChannels(lastAudioTag.getChannels());
			break;
		case NELLY_16: // TODO 未チェック
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_NELLYMOSER);
			decoder.setSampleRate(16000);
			decoder.setTimeBase(IRational.make(1, lastAudioTag.getSampleRate()));
			decoder.setChannels(lastAudioTag.getChannels());
			break;
		case SPEEX:
			// FLVのspeexは16000hz + 1Channelのみになってる。
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_SPEEX);
			decoder.setSampleRate(16000);
			decoder.setTimeBase(IRational.make(1, lastAudioTag.getSampleRate()));
			decoder.setChannels(1);
			break;
		case PCM:
		case ADPCM:
		case G711_A:
		case G711_U:
		case RESERVED:
		case DEVICE_SPECIFIC:
			throw new RuntimeException(lastAudioTag.getCodec() + "の変換は未実装です。");
		default:
			return null;
		}
		return decoder;
	}
}
