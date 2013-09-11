package com.ttProject.xuggle.flv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.aac.DecoderSpecificInfo;
import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
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
 * @author taktod
 */
public class FlvDepacketizer {
	private SequenceParameterSet sps = null;
	private PictureParameterSet pps = null;
	private DecoderSpecificInfo dsi;
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
			break;
		case CODEC_ID_MP3:
			dsi = null;
			// mp3の場合
			break;
		default:
			throw new RuntimeException(encoder.getCodecID() + "のflvTag化は未実装です。");
		}
		return null;
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
		ByteBuffer buffer = packet.getByteBuffer();
		videoTag.setSize(12 + 4 + buffer.remaining());
		// これどうするかな(timestampはipacketのデータからつくれそう。ただし、音声の場合はframeからカウントしないとだめっぽい。)
		videoTag.setTimestamp((int)(packet.getTimeStamp() * packet.getTimeBase().getDouble() * 1000));
		videoTag.setRawData(buffer);
		tagList.add(videoTag);
		return tagList;
	}
	/**
	 * aacのtagに変換します。
	 * TODO ついでにmshタグについて確認して、前のデータと一致する場合は応答しないようにしておく。
	 * @param packet
	 * @return
	 */
	private List<Tag> getAVCTag(IPacket packet) throws Exception {
		List<Tag> tagList = new ArrayList<Tag>();
		NalAnalyzer nalAnalyzer = new NalAnalyzer();
		IReadChannel byteDataChannel = new ByteReadChannel(packet.getByteBuffer());
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
				ByteBuffer buffer = configData.makeConfigData(sps, pps);
				videoTag.setSize(12 + 4 + 4 + buffer.remaining());
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
				ByteBuffer buffer = sliceIdr.getBuffer();
				videoTag.setSize(12 + 4 + 4 + 4 + buffer.remaining());
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
				ByteBuffer buffer = slice.getBuffer();
				videoTag.setSize(12 + 4 + 4 + 4 + buffer.remaining());
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
}
