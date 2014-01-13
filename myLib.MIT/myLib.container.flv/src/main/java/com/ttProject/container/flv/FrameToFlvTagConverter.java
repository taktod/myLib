package com.ttProject.container.flv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.frame.adpcmswf.AdpcmswfFrame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.frame.flv1.type.DisposableInterFrame;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.h264.type.Slice;
import com.ttProject.frame.h264.type.SliceIDR;
import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.frame.vp6.Vp6Frame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * frameデータからflvTagを生成して応答する変換動作
 * @author taktod
 */
public class FrameToFlvTagConverter {
	/** ロガー */
	private Logger logger = Logger.getLogger(FrameToFlvTagConverter.class);
	// msh用のデータ変更があったら応答しておきたいところ。
	private DecoderSpecificInfo  dsi = null;
	private SequenceParameterSet sps = null;
	private PictureParameterSet  pps = null;
	
	private FlvTagReader reader = new FlvTagReader();
	/**
	 * FlvTagリストを取得します。
	 * @return
	 */
	public List<FlvTag> getTags(IFrame frame) throws Exception {
		if(frame instanceof VideoFrame) {
			// 映像フレームの処理
			return getVideoTags((VideoFrame)frame);
		}
		else if(frame instanceof AudioFrame) {
			// 音声フレームの処理
			return getAudioTags((AudioFrame)frame);
		}
		throw new Exception("音声でも映像でもないフレームを検知しました。" + frame.toString());
	}
	/**
	 * 音声フレームについて処理します
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getAudioTags(AudioFrame frame) throws Exception {
		List<FlvTag> result = new ArrayList<FlvTag>();
		ByteBuffer frameBuffer = null;
		if(frame instanceof AacFrame) {
			Bit8 sequenceHeaderFlag = new Bit8();
			BitConnector connector = new BitConnector();
			// aacではdecoderSpecificInfoの調整を実施する必要あり
			Frame aacFrame = (Frame) frame;
			DecoderSpecificInfo dsi = aacFrame.getDecoderSpecificInfo();
			// 新規にdecoderSpecificInfoの追記が必要
			if(this.dsi == null || this.dsi.getData().compareTo(dsi.getData()) != 0) {
				this.dsi = dsi;
				sequenceHeaderFlag.set(0);
				frameBuffer = BufferUtil.connect(
						connector.connect(sequenceHeaderFlag),
						dsi.getData()
				);
				result.add(getAudioTag(frame, frameBuffer));
			}
			// 通常のframe
			sequenceHeaderFlag.set(1);
			ByteBuffer frameData = frame.getData();
			frameData.position(7);
			frameBuffer = BufferUtil.connect(
					connector.connect(sequenceHeaderFlag),
					frameData
			);
		}
		else if(frame instanceof Mp3Frame) {
			frameBuffer = frame.getData();
		}
		else if(frame instanceof NellymoserFrame) {
			frameBuffer = frame.getData();
		}
		else if(frame instanceof SpeexFrame) {
			frameBuffer = frame.getData();
		}
		else if(frame instanceof AdpcmswfFrame) {
			frameBuffer = frame.getData();
		}
		else {
			throw new Exception("未対応なaudioFrameでした:" + frame);
		}
		result.add(getAudioTag(frame, frameBuffer));
		// audioTagを読み込ませます。
		return result;
	}
	/**
	 * frameと保存するデータからaudioTagを作成して応答します。
	 * @param frame
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	private FlvTag getAudioTag(AudioFrame frame, ByteBuffer buffer) throws Exception {
		Bit8  tagType      = new Bit8(0x08);
		Bit24 size         = new Bit24();
		Bit24 timestamp    = new Bit24();
		Bit8  timestampExt = new Bit8();
		Bit24 streamId     = new Bit24();
		Bit4  codecId      = null;
		Bit2  sampleRate   = null;
		Bit1  bitCount     = null;
		Bit1  channels     = null;
		Bit32 preSize      = new Bit32();
		// codecIdと拡張データについて調整しておく必要あり。
		codecId = new Bit4();
		if(frame instanceof AacFrame) {
			codecId.set(CodecType.getAudioCodecNum(CodecType.AAC));
		}
		else if(frame instanceof Mp3Frame) {
			if(frame.getSampleRate() == 8000) {
				// mp3 8はデータが手元にないので、どうなるかわからない。
				// とりあえず0xD2にでもしておくか・・・
				codecId.set(CodecType.getAudioCodecNum(CodecType.MP3_8));
				sampleRate = new Bit2();
			}
			else {
				codecId.set(CodecType.getAudioCodecNum(CodecType.MP3));
			}
		}
		else if(frame instanceof NellymoserFrame) {
			if(frame.getSampleRate() == 16000) {
				// nelly16 0x42
				codecId.set(CodecType.getAudioCodecNum(CodecType.NELLY_16));
				sampleRate = new Bit2();
			}
			else if(frame.getSampleRate() == 8000) {
				// nelly8の場合0x52になる。
				codecId.set(CodecType.getAudioCodecNum(CodecType.NELLY_8));
				sampleRate = new Bit2();
			}
			else {
				codecId.set(CodecType.getAudioCodecNum(CodecType.NELLY));
			}
		}
		else if(frame instanceof SpeexFrame) {
			// 0xB6みたい。
			codecId.set(CodecType.getAudioCodecNum(CodecType.SPEEX));
			if(frame.getSampleRate() != 16000) {
				throw new Exception("speexのsampleRateは16kHzのみサポートします。");
			}
			if(frame.getChannel() == 1) {
				throw new Exception("speexはmonoralのみサポートします。");
			}
			sampleRate = new Bit2(1);
		}
		else if(frame instanceof AdpcmswfFrame) {
			codecId.set(CodecType.getAudioCodecNum(CodecType.ADPCM));
		}
		else {
			throw new Exception("未対応なaudioFrameでした:" + frame);
		}
		if(channels == null) {
			channels = new Bit1();
			switch(frame.getChannel()) {
			case 1:
				channels.set(0);
				break;
			case 2:
				channels.set(1);
				break;
			default:
				throw new Exception("音声チャンネル数がflvに適合しないものでした。");
			}
		}
		if(bitCount == null) {
			bitCount = new Bit1();
			switch(frame.getBit()) {
			case 8:
				bitCount.set(0);
				break;
			case 16:
				bitCount.set(1);
				break;
			default:
				bitCount.set(1);
//				throw new Exception("ビット深度が適合しないものでした。:" + frame.getBit());
			}
		}
		if(sampleRate == null) {
			sampleRate = new Bit2();
			switch((int)(frame.getSampleRate() / 100)) {
			case 55:
				sampleRate.set(0);
				break;
			case 110:
				sampleRate.set(1);
				break;
			case 220:
				sampleRate.set(2);
				break;
			case 441:
				sampleRate.set(3);
				break;
			default:
				throw new Exception("frameRateが適合しないものでした。");
			}
		}
		BitConnector connector = new BitConnector();
		ByteBuffer mediaData = BufferUtil.connect(
				connector.connect(codecId, sampleRate, bitCount, channels),
				buffer);
		size.set(mediaData.remaining());
		preSize.set(size.get() + 11);
		int time = (int)(frame.getPts() * 1000 / frame.getTimebase());
		timestamp.set(time & 0x00FFFFFF);
		timestampExt.set(time >> 24);
		ByteBuffer tagBuffer = BufferUtil.connect(
				connector.connect(tagType, size, timestamp, timestampExt, streamId),
				mediaData,
				connector.connect(preSize));
		ByteReadChannel channel = new ByteReadChannel(tagBuffer);
		FlvTag tag = (FlvTag)reader.read(channel);
		return tag;
	}
	/**
	 * 映像フレームについて処理します
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getVideoTags(VideoFrame frame) throws Exception {
		List<FlvTag> result = new ArrayList<FlvTag>();
		ByteBuffer frameBuffer = null;
		BitConnector connector = new BitConnector();
		if(frame instanceof Flv1Frame) {
			frameBuffer = frame.getData();
		}
		else if(frame instanceof Vp6Frame) {
			// vp6Aとvp6で動作が違います。
			// とりあえずvp6Aは放置しておく。(判定するすべがないし、そもそも流通ないし)
			Bit4  horizontalAdjustment = new Bit4();
			Bit4  verticalAdjustment   = new Bit4();
//			Bit32 offsetToAlpha        = null;
			frameBuffer = BufferUtil.connect(connector.connect(horizontalAdjustment, verticalAdjustment),
					frame.getData()
			);
		}
		else if(frame instanceof H264Frame) {
			Bit8  packetType = new Bit8(); // mshの場合は0,通常フレームなら1,データ終端なら2
			Bit24 dts        = new Bit24(); // dts値
			if(frame instanceof SliceIDR) {
				// sps ppsを取り出してconfigデータをつくりたいところ
				logger.info("keyFrame検知");
				SliceIDR sliceIDR = (SliceIDR)frame;
				if(sps == null || pps == null
				|| sps.getData().compareTo(sliceIDR.getSps().getData()) != 0
				|| pps.getData().compareTo(sliceIDR.getPps().getData()) != 0) {
					logger.info("h264Config作成が必要");
					sps = sliceIDR.getSps();
					pps = sliceIDR.getPps();
					ConfigData configData = new ConfigData();
					packetType.set(0);
					dts.set(0);
					frameBuffer = BufferUtil.connect(connector.connect(
							packetType, dts),
							configData.makeConfigData(sps, pps));
					result.add(getVideoTag(frame, frameBuffer));
				}
			}
			else if(frame instanceof Slice) {
				// sps ppsが未設定なら処理しない
				if(sps == null || pps == null) {
					return null;
				}
				logger.info("innerFrame検知");
			}
			else {
				// slice もしくは sliceIDRでないデータは処理しない
				return null;
			}
			Bit32 nalSize = new Bit32(frame.getSize());
			packetType.set(1);
			dts.set((int)(frame.getDts() * 1000 / frame.getTimebase()));
			frameBuffer = BufferUtil.connect(connector.connect(packetType, dts, nalSize),
					frame.getData()
			);
		}
		else {
			throw new Exception("想定外のframeです。:" + frame);
		}
		result.add(getVideoTag(frame, frameBuffer));
		return result;
	}
	/**
	 * 映像tagを作成します
	 * @param frame
	 * @param buffer
	 * @return
	 */
	private FlvTag getVideoTag(VideoFrame frame, ByteBuffer buffer) throws Exception {
		Bit8 tagType      = new Bit8(0x09);
		Bit24 size        = new Bit24();
		Bit24 timestamp   = new Bit24();
		Bit8 timestampExt = new Bit8();
		Bit24 streamId    = new Bit24();
		Bit4 frameType    = new Bit4();
		Bit4 codecId      = new Bit4();
		Bit32 preSize     = new Bit32();
		if(frame.isKeyFrame()) {
			frameType.set(1);
		}
		else {
			frameType.set(2);
		}
		if(frame instanceof Flv1Frame) {
			codecId.set(CodecType.getVideoCodecNum(CodecType.FLV1));
			if(frame instanceof DisposableInterFrame) {
				frameType.set(3);
			}
		}
		else if(frame instanceof Vp6Frame) {
			codecId.set(CodecType.getVideoCodecNum(CodecType.ON2VP6));
		}
		else if(frame instanceof H264Frame) {
			codecId.set(CodecType.getVideoCodecNum(CodecType.H264));
		}
		BitConnector connector = new BitConnector();
		ByteBuffer mediaData = BufferUtil.connect(
				connector.connect(frameType, codecId),
				buffer);
		size.set(mediaData.remaining());
		preSize.set(size.get() + 11);
		int time = (int)(frame.getPts() * 1000 / frame.getTimebase());
		timestamp.set(time & 0x00FFFFFF);
		timestampExt.set(time >> 24);
		ByteBuffer tagBuffer = BufferUtil.connect(
				connector.connect(tagType, size, timestamp, timestampExt, streamId),
				mediaData,
				connector.connect(preSize));
		ByteReadChannel channel = new ByteReadChannel(tagBuffer);
		FlvTag tag = (FlvTag)reader.read(channel);
		return tag;
	}
}
