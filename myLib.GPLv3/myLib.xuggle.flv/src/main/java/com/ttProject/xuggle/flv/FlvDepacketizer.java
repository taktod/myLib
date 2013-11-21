package com.ttProject.xuggle.flv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.h264.ConfigData;
import com.ttProject.media.h264.Frame;
import com.ttProject.media.h264.NalAnalyzer;
import com.ttProject.media.h264.frame.PictureParameterSet;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.media.h264.frame.Slice;
import com.ttProject.media.h264.frame.SliceIDR;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * IPacketをflvTagに戻す処理
 * 変換を戻す場合はそれぞれのメディアトラック用のDepacketizerをつくらないとだめ。
 * avcで複数出力するといった場合に出力できないことがありえる。
 * @author taktod
 */
public class FlvDepacketizer {
	private SequenceParameterSet sps = null;
	private PictureParameterSet pps = null;
	private DecoderSpecificInfo dsi = null;
	/**
	 * packetからtagを取り出す。
	 * @param encoder
	 * @param packet
	 * @return
	 */
	public List<Tag> getTag(IStreamCoder encoder, IPacket packet) throws Exception {
		// パケットが完成していなかったら処理しない。
		if(!packet.isComplete()) {
			return null;
		}
		switch(encoder.getCodecID()) {
		case CODEC_ID_FLV1:
			sps = null;
			pps = null;
			// flv1の場合
			return getH263Tag(packet);
		case CODEC_ID_H264:
			// avcの場合
			return getAVCTag(packet);
		case CODEC_ID_AAC:
			// aacの場合
			return getAacTag(packet);
		case CODEC_ID_MP3:
			dsi = null;
			// mp3の場合
			return getMp3Tag(encoder, packet);
		default:
			throw new RuntimeException(encoder.getCodecID() + "のflvTag化は未実装です。");
		}
	}
	/**
	 * h263のtagに変換します。
	 * @param packet
	 * @return
	 */
	private List<Tag> getH263Tag(IPacket packet) {
		List<Tag> tagList = new ArrayList<Tag>();
		VideoTag videoTag = new VideoTag();
		videoTag.setCodec(CodecType.H263);
		videoTag.setFrameType(packet.isKey());
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData().getByteArray(0, packet.getSize()));
		// これどうするかな(timestampはipacketのデータからつくれそう。ただし、音声の場合はframeからカウントしないとだめっぽい。)
		videoTag.setTimestamp((int)(packet.getTimeStamp() * packet.getTimeBase().getDouble() * 1000));
		videoTag.setRawData(buffer);
		tagList.add(videoTag);
		return tagList;
	}
	/**
	 * avcのtagに変換します。
	 * TODO ついでにmshタグについて確認して、前のデータと一致する場合は応答しないようにしておく。
	 * @param packet
	 * @return
	 */
	private List<Tag> getAVCTag(IPacket packet) throws Exception {
		List<Tag> tagList = new ArrayList<Tag>();
		NalAnalyzer nalAnalyzer = new NalAnalyzer();
		IReadChannel byteDataChannel = new ByteReadChannel(packet.getData().getByteArray(0, packet.getSize()));
		// キーパケットである場合
		Frame frame = null;
		Boolean spsUpdated = null;
		Boolean ppsUpdated = null;
		while((frame = nalAnalyzer.analyze(byteDataChannel)) != null) {
			// データがみつかった。
			if(frame instanceof SequenceParameterSet) {
				// すでにもっているspsと一致するか確認
				SequenceParameterSet newSps = (SequenceParameterSet) frame;
				// まえのspsと比較して内容が同じならスキップする。
				if(sps != null) {
					spsUpdated = sps.hashCode() != newSps.hashCode();
					sps = newSps;
				}
				else {
					sps = newSps;
					spsUpdated = true;
				}
			}
			if(frame instanceof PictureParameterSet) {
				// すでにもっているppsと一致するか確認
				PictureParameterSet newPps = (PictureParameterSet) frame;
				if(pps != null) {
					ppsUpdated = pps.hashCode() != newPps.hashCode();
					pps = newPps;
				}
				else {
					pps = newPps;
					ppsUpdated = true;
				}
			}
			if(spsUpdated != null && ppsUpdated != null && (spsUpdated || ppsUpdated)) {
				// spsかppsが更新されている場合
				ConfigData configData = new ConfigData();
				// これが内部データ
				spsUpdated = null;
				ppsUpdated = null;
				VideoTag videoTag = new VideoTag();
				videoTag.setCodec(CodecType.AVC);
				videoTag.setFrameType(true);
				videoTag.setMSHFlg(true);
				videoTag.setTimestamp((int)(packet.getTimeStamp() * packet.getTimeBase().getDouble() * 1000));
				ByteBuffer buffer = configData.makeConfigData(sps, pps);
				videoTag.setData(new ByteReadChannel(buffer), buffer.remaining());
				tagList.add(videoTag);
			}
			if(frame instanceof SliceIDR) {
				// キーフレームだった場合の処理
				SliceIDR sliceIdr = (SliceIDR) frame;
				// キーフレームをつくっていれる必要あり。
				VideoTag videoTag = new VideoTag();
				videoTag.setCodec(CodecType.AVC);
				videoTag.setFrameType(true);
				videoTag.setMSHFlg(false);
				videoTag.setTimestamp((int)(packet.getTimeStamp() * packet.getTimeBase().getDouble() * 1000));
				ByteBuffer buffer = sliceIdr.getData();
				ByteBuffer buf = ByteBuffer.allocate(7 + buffer.remaining());
				buf.put((byte)0);
				buf.put((byte)0);
				buf.put((byte)0);
				buf.putInt(buffer.remaining());
				buf.put(buffer);
				buf.flip();
				videoTag.setRawData(buf);
				tagList.add(videoTag);
			}
			if(frame instanceof Slice) {
				Slice slice = (Slice) frame;
				VideoTag videoTag = new VideoTag();
				videoTag.setCodec(CodecType.AVC);
				videoTag.setFrameType(false);
				videoTag.setMSHFlg(false);
				videoTag.setTimestamp((int)(packet.getTimeStamp() * packet.getTimeBase().getDouble() * 1000));
				ByteBuffer buffer = slice.getData();
				ByteBuffer buf = ByteBuffer.allocate(7 + buffer.remaining());
				buf.put((byte)0);
				buf.put((byte)0);
				buf.put((byte)0);
				buf.putInt(buffer.remaining());
				buf.put(buffer);
				buf.flip();
				videoTag.setRawData(buf);
				tagList.add(videoTag);
			}
		}
		return tagList;
	}
	/**
	 * mp3のtagに変換します。
	 * @param encoder
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private List<Tag> getMp3Tag(IStreamCoder encoder, IPacket packet) throws Exception {
		List<Tag> tagList = new ArrayList<Tag>();
		AudioTag audioTag = new AudioTag();
		switch(encoder.getSampleRate()) {
		case 8000:
			audioTag.setCodec(CodecType.MP3_8);
			break;
		case 44100:
		case 22050:
		case 11025:
			audioTag.setCodec(CodecType.MP3);
			break;
		default:
			throw new RuntimeException("知らないmp3のフォーマットでした。");
		}
		switch(encoder.getChannels()) {
		case 1:
			audioTag.setChannels((byte)1);
			break;
		case 2:
			audioTag.setChannels((byte)2);
			break;
		default:
			throw new RuntimeException("チャンネル数が不正です。:" + encoder.getChannels());
		}
		audioTag.setSampleRate(encoder.getSampleRate());
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData().getByteArray(0, packet.getSize()));
		audioTag.setTimestamp((int)(packet.getTimeStamp() * packet.getTimeBase().getDouble() * 1000));
		audioTag.setRawData(buffer);
		tagList.add(audioTag);
		return tagList;
	}
	/**
	 * aacのTagに変換します。
	 * @param packet
	 * @return
	 * @throws Exception
	 */
	private List<Tag> getAacTag(IPacket packet) throws Exception {
		List<Tag> tagList = new ArrayList<Tag>();
		//packetデータからAacをとりあえずつくる。
		FrameAnalyzer analyzer = new FrameAnalyzer();
		Object aacFrame = analyzer.analyze(new ByteReadChannel(packet.getData().getByteArray(0, packet.getSize())));
		if(!(aacFrame instanceof Aac)) {
			throw new RuntimeException("不正なデータでした。");
		}
		Aac aac = (Aac) aacFrame;
		DecoderSpecificInfo dsi = new DecoderSpecificInfo();
		dsi.analyze(aac);
		// いままでのdsiと一致するか確認する必要あり。
		boolean dsiUpdate = false;
		if(this.dsi == null) {
			this.dsi = dsi;
			dsiUpdate = true;
		}
		else if(this.dsi.getInfoBuffer().hashCode() != dsi.getInfoBuffer().hashCode()){
			this.dsi = dsi;
			dsiUpdate = true;
		}
		if(dsiUpdate) {
			// mshをつくる必要あり。
			AudioTag audioMshTag = new AudioTag();
			audioMshTag.setCodec(CodecType.AAC);
			switch(aac.getChannelConfiguration()) {
			case 1:
				audioMshTag.setChannels((byte)1);
				break;
			case 2:
				audioMshTag.setChannels((byte)2);
				break;
			default:
				throw new RuntimeException("対応していないチャンネル数です。");
			}
			audioMshTag.setSampleRate((int)(aac.getSamplingRate() * 1000));
			audioMshTag.setMSHFlg(true);
			audioMshTag.setRawData(dsi.getInfoBuffer());
			tagList.add(audioMshTag);
		}
		AudioTag audioTag = new AudioTag();
		audioTag.setCodec(CodecType.AAC);
		switch(aac.getChannelConfiguration()) {
		case 1:
			audioTag.setChannels((byte)1);
			break;
		case 2:
			audioTag.setChannels((byte)2);
			break;
		default:
			throw new RuntimeException("対応していないチャンネル数です。");
		}
		audioTag.setSampleRate((int)(aac.getSamplingRate() * 1000));
		audioTag.setMSHFlg(false);
		audioTag.setTimestamp((int)(packet.getTimeStamp() * packet.getTimeBase().getDouble() * 1000));
		audioTag.setRawData(aac.getDataBuffer());
		tagList.add(audioTag);
		// タグを応答すればOK
		return tagList;
	}
}
