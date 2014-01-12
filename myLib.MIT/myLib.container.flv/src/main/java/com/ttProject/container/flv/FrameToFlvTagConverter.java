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
import com.ttProject.frame.adpcmswf.AdpcmswfFrame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
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
		Bit8  tagType = new Bit8(0x08);
		Bit24 size = new Bit24();
		Bit24 timestamp = new Bit24();
		Bit8  timestampExt = new Bit8();
		Bit24 streamId = new Bit24();
		Bit4  codecId = null;
		Bit2  sampleRate = null;
		Bit1  bitCount = null;
		Bit1  channels = null;
		Bit8  sequenceHeaderFlag = null;
		Bit32 preSize = new Bit32();
		ByteBuffer frameBuffer = null;
		// codecIdと拡張データについて調整しておく必要あり。
		codecId = new Bit4();
		if(frame instanceof AacFrame) {
			codecId.set(CodecType.getAudioCodecNum(CodecType.AAC));
			sequenceHeaderFlag = new Bit8(1); // mshなら0になる、通常のtagを書き込む予定なので1にしておく。
			// ここだけ特殊なことしないとだめ。
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
			frameBuffer = frame.getData();
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
			frameBuffer = frame.getData();
		}
		else if(frame instanceof SpeexFrame) {
			// 0xB6みたい。
			codecId.set(CodecType.getAudioCodecNum(CodecType.SPEEX));
			sampleRate = new Bit2(1);
			frameBuffer = frame.getData();
		}
		else if(frame instanceof AdpcmswfFrame) {
			codecId.set(CodecType.getAudioCodecNum(CodecType.ADPCM));
			frameBuffer = frame.getData();
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
				throw new Exception("ビット深度が適合しないものでした。");
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
				connector.connect(codecId, sampleRate, bitCount, channels, sequenceHeaderFlag),
				frameBuffer);
		size.set(mediaData.remaining());
		preSize.set(size.get() + 11);
		int time = (int)(frame.getPts() * 1000 / frame.getTimebase());
		timestamp.set(time & 0x00FFFFFF);
		timestampExt.set(time >> 24);
		ByteBuffer tagBuffer = BufferUtil.connect(
				connector.connect(tagType, size, timestamp, timestampExt, streamId),
				mediaData,
				connector.connect(preSize));
//		logger.info(HexUtil.toHex(tagBuffer, true));
		ByteReadChannel channel = new ByteReadChannel(tagBuffer);
		FlvTag tag = (FlvTag)reader.read(channel);
//		logger.info(tag);
		result.add(tag);
		// audioTagを読み込ませます。
		return result;
	}
	/**
	 * 映像フレームについて処理します
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getVideoTags(VideoFrame frame) {
		if(frame instanceof Flv1Frame) {
			
		}
		else if(frame instanceof Vp6Frame) {
			
		}
		else if(frame instanceof H264Frame) {
			
		}
		return null;
	}
}
