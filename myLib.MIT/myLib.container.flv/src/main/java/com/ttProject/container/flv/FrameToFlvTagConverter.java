package com.ttProject.container.flv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.frame.flv1.type.DisposableInterFrame;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.h264.type.Slice;
import com.ttProject.frame.h264.type.SliceIDR;
import com.ttProject.frame.vp6.Vp6Frame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.unit.extra.BitConnector;
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
		// aacの場合はmshチェックをしておく
		if(frame instanceof AacFrame) {
			Frame aacFrame = (Frame) frame;
			DecoderSpecificInfo dsi = aacFrame.getDecoderSpecificInfo();
			if(this.dsi == null || this.dsi.getData().compareTo(dsi.getData()) != 0) {
				this.dsi = dsi;
				AudioTag audioTag = new AudioTag();
				audioTag.setAacMediaSequenceHeader(aacFrame, dsi);
				result.add(audioTag);
			}
		}
		// audioTagをつくっておく
		AudioTag audioTag = new AudioTag();
		audioTag.addFrame(frame);
		result.add(audioTag);
		return result;
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
