package com.ttProject.xuggle.flv;

import java.nio.ByteBuffer;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.h264.ConfigData;
import com.ttProject.media.h264.DataNalAnalyzer;
import com.ttProject.media.h264.Frame;
import com.ttProject.media.h264.frame.PictureParameterSet;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.media.h264.frame.Slice;
import com.ttProject.media.h264.frame.SliceIDR;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * flvTagをIPacketに変換するための動作
 * @author taktod
 *
 */
public class FlvPacketizer {
	private VideoTag lastVideoTag;
	private SequenceParameterSet sps = null;
	private PictureParameterSet pps = null;

	private AudioTag lastAudioTag;
	private DecoderSpecificInfo dsi = null;
	/**
	 * tagからIPacketを取り出す
	 * @param tag
	 * @return
	 */
	public IPacket getPacket(Tag tag) throws Exception {
		if(tag instanceof VideoTag) {
			lastVideoTag = (VideoTag) tag;
			switch(lastVideoTag.getCodec()) {
			case JPEG:
				sps = null;
				pps = null;
				throw new RuntimeException("JPEGの変換は未実装です。");
			case H263:
				sps = null;
				pps = null;
				return getH263Packet(lastVideoTag);
			case SCREEN:
				sps = null;
				pps = null;
				throw new RuntimeException("SCREENの変換は未実装です。");
			case ON2VP6:
				sps = null;
				pps = null;
				return getVP6Packet(lastVideoTag);
			case ON2VP6_ALPHA:
				sps = null;
				pps = null;
				throw new RuntimeException("vp6 alphaの変換は未実装です。");
			case SCREEN_V2:
				sps = null;
				pps = null;
				throw new RuntimeException("Screen V2の変換は未実装です。");
			case AVC:
				return getAVCPacket(lastVideoTag);
			default:
				break;
			}
		}
		else if(tag instanceof AudioTag) {
			lastAudioTag = (AudioTag) tag;
			switch(lastAudioTag.getCodec()) {
			case AAC:
				return getAACPacket(lastAudioTag);
			case MP3_8:
			case MP3:
				dsi = null;
				return getMp3Packet(lastAudioTag);
			case NELLY_16:
			case NELLY_8:
			case NELLY:
				dsi = null;
				return getNellyPacket(lastAudioTag);
			case SPEEX:
				dsi = null;
				return getSpeexPacket(lastAudioTag);
			case PCM:
			case ADPCM:
			case G711_A:
			case G711_U:
			case RESERVED:
			case DEVICE_SPECIFIC:
				dsi = null;
				throw new RuntimeException(lastAudioTag.getCodec() + "の変換は未実装です。");
			default:
				return null;
			}
		}
		return null;
	}
	/**
	 * h263用のIPacketを生成します。
	 * @param tag
	 * @return
	 */
	private IPacket getH263Packet(VideoTag tag) {
		IPacket packet = IPacket.make();
		ByteBuffer buffer = tag.getRawData();
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setFlags(0);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		packet.setKeyPacket(tag.isKeyFrame());
		return packet;
	}
	/**
	 * vp6用のIPacketを生成します。
	 * @param tag
	 * @return
	 */
	private IPacket getVP6Packet(VideoTag tag) {
		IPacket packet = IPacket.make();
		ByteBuffer buffer = tag.getRawData();
		int size = buffer.remaining();
		byte first = buffer.get();
		byte[] data = new byte[size];
		buffer.get(data, 0, size - 1);
		data[data.length - 1] = first;
		IBuffer bufData = IBuffer.make(null, data, 0, size);
		packet.setData(bufData);
		packet.setFlags(0);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		packet.setKeyPacket(tag.isKeyFrame());
		return packet;
	}
	/**
	 * avc用のIPacketを生成します。
	 * @param tag
	 * @return
	 */
	private IPacket getAVCPacket(VideoTag tag) throws Exception {
		if(tag.isMediaSequenceHeader()) {
			// spsとppsを抜き出す。
			ConfigData configData = new ConfigData();
			IReadChannel rawData = new ByteReadChannel(tag.getRawData());
			rawData.position(3);
			for(Frame nal : configData.getNals(rawData)) {
				if(nal instanceof SequenceParameterSet) {
					sps = (SequenceParameterSet) nal;
				}
				else if(nal instanceof PictureParameterSet) {
					pps = (PictureParameterSet) nal;
				}
			}
			return null;
		}
		if(tag.isEndOfSequence()) {
			return null;
		}
		if(sps == null || pps == null) {
			throw new RuntimeException("spsもしくはppsが決定していません。");
		}
		// mshではないので、実データである。
		IPacket packet = IPacket.make();
		ByteBuffer rawData = tag.getRawData();
		rawData.position(3); // ３の位置に移動(ここからDataNalAnalyzerにいれるとnalが取り出せる)
		DataNalAnalyzer dataAnalyzer = new DataNalAnalyzer();
		IReadChannel rawDataChannel = new ByteReadChannel(rawData);
		ByteBuffer buffer = null;
		Frame avcFrame = null;
		if(tag.isKeyFrame()) {
			while((avcFrame = dataAnalyzer.analyze(rawDataChannel)) != null) {
				if(avcFrame instanceof SliceIDR) {
					break;
				}
			}
			ByteBuffer spsData = sps.getData();
			ByteBuffer ppsData = pps.getData();
			ByteBuffer sliceIDRData = avcFrame.getData();
			buffer = ByteBuffer.allocate(4 + spsData.remaining()
					+ 4 + ppsData.remaining()
					+ 4 + sliceIDRData.remaining());
			buffer.putInt(1);
			buffer.put(spsData);
			buffer.putInt(1);
			buffer.put(ppsData);
			buffer.putInt(1);
			buffer.put(sliceIDRData);
			buffer.flip();
		}
		else {
			while((avcFrame = dataAnalyzer.analyze(rawDataChannel)) != null) {
				if(avcFrame instanceof Slice) {
					break;
				}
			}
			ByteBuffer sliceData =avcFrame.getData();
			buffer = ByteBuffer.allocate(4 + sliceData.remaining());
			buffer.putInt(1);
			buffer.put(sliceData);
			buffer.flip();
		}
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setFlags(1);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		packet.setKeyPacket(tag.isKeyFrame());
		return packet;
	}
	/**
	 * 最後に応答したpacketに対応する映像coderを作成し応答する。
	 * @return 
	 */
	public IStreamCoder createVideoDecoder() throws Exception {
		// まだデータがわからない場合はnullを返す
		if(lastVideoTag == null) {
			return null;
		}
		IStreamCoder decoder = null;
		switch(lastVideoTag.getCodec()) {
		case JPEG:
			throw new RuntimeException("JPEGの変換は未実装です。");
		case H263:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_FLV1);
			decoder.setTimeBase(IRational.make(1, 1000));
			break;
		case SCREEN:
			throw new RuntimeException("SCREENの変換は未実装です。");
		case ON2VP6:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_VP6F);
			decoder.setTimeBase(IRational.make(1, 1000));
			break;
		case ON2VP6_ALPHA:
			throw new RuntimeException("vp6 alphaの変換は未実装です。");
		case SCREEN_V2:
			throw new RuntimeException("Screen V2の変換は未実装です。");
		case AVC:
			decoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_H264);
			decoder.setTimeBase(IRational.make(1, 1000));
			break;
		default:
			return null;
		}
		if(decoder.open(null, null) < 0) {
			throw new Exception("デコーダーが開けませんでした。");
		}
		return decoder;
	}
	private IPacket getMp3Packet(AudioTag tag) throws Exception {
		IPacket packet = IPacket.make();
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
	private IPacket getNellyPacket(AudioTag tag) throws Exception {
		IPacket packet = IPacket.make();
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
	private IPacket getSpeexPacket(AudioTag tag) throws Exception {
		IPacket packet = IPacket.make();
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
	 * @return
	 */
	private IPacket getAACPacket(AudioTag tag) throws Exception {
		IPacket packet = IPacket.make();
		if(tag.isMediaSequenceHeader()) {
			dsi = new DecoderSpecificInfo();
			dsi.analyze(new ByteReadChannel(tag.getRawData()));
			return null;
		}
		if(dsi == null) {
			throw new RuntimeException("decoderSpecificInfoが決定していません");
		}
		ByteBuffer rawData = tag.getRawData();
		int size = rawData.remaining();
		Aac aac = new Aac(size, dsi);
		aac.setData(rawData);
		ByteBuffer buffer = aac.getBuffer();
		size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setDts(tag.getTimestamp());
		packet.setPts(tag.getTimestamp());
		packet.setTimeBase(IRational.make(1, 1000));
		packet.setComplete(true, size);
		return packet;
	}
	/**
	 * 最後に応答したpacketに対応する音声coderを作成し応答する。
	 * @return
	 */
	public IStreamCoder createAudioDecoder() throws Exception {
		if(lastAudioTag == null) {
			return null;
		}
		IStreamCoder decoder = null;
		switch(lastAudioTag.getCodec()) {
		case AAC:
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
		if(decoder.open(null, null) < 0) {
			throw new Exception("デコーダーが開けませんでした。");
		}
		return decoder;
	}
}
