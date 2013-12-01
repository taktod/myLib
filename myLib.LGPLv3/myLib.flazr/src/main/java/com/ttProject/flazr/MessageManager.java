package com.ttProject.flazr;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.MessageType;
import com.flazr.rtmp.message.Metadata;
import com.flazr.rtmp.message.Video;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AggregateTag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.media.flv.tag.VideoTag;

/**
 * rtmpメッセージを操作する動作
 * @author taktod
 */
public class MessageManager {
	/**
	 * rtmpメッセージから、myLib.flvのtagを作成して応答します。
	 * @param message
	 * @return
	 */
	public Tag getTag(RtmpMessage message) {
		RtmpHeader header = message.getHeader();
		if(header.isAggregate()) {
			// tagのリストとして応答してやる必要がある。
			return convertToAggregateTag(message);
		}
		else if(header.isAudio()) {
			return convertToAudioTag((Audio)message);
		}
		else if(header.isVideo()) {
			return convertToVideoTag((Video)message);
		}
		else if(message instanceof MetadataAmf3) {
			return convertToMetaTag((MetadataAmf3)message);
		}
		else if(header.isMetadata() && message instanceof Metadata) {
			return convertToMetaTag((Metadata)message);
		}
		// どのデータにも当てはまらなかったのでnullを応答します。
		return null;
	}
	/**
	 * aggregateTagを応答します。(videoとaudioの集合タグになります)
	 * @param message
	 * @return
	 */
	private AggregateTag convertToAggregateTag(RtmpMessage message) {
		final RtmpHeader header = message.getHeader();
		int difference = -1;
		final ChannelBuffer in = message.encode();
		AggregateTag aTag = new AggregateTag();
		while(in.readable()) {
			// messageTypeとsizeと時刻がほしい。
			final MessageType messageType = MessageType.valueToEnum(in.readByte());
			final int size = in.readMedium();
			final int time = in.readMedium() + ((in.readByte() & 0xFF) << 24);
			if(difference == -1) {
				difference = time - header.getTime();
			}
			final RtmpHeader subHeader = new RtmpHeader(messageType, time - difference, size);
			in.skipBytes(3);
			ChannelBuffer data = in.readBytes(size);
			in.skipBytes(4);
			Tag tag = null;
			if(subHeader.isAudio()) {
				tag = convertToAudioTag(subHeader, data);
			}
			else if(subHeader.isVideo()) {
				tag = convertToVideoTag(subHeader, data);
			}
			if(tag != null) {
				aTag.add(tag);
			}
		}
		if(aTag.size() == 0) {
			return null;
		}
		else {
			return aTag;
		}
	}
	/**
	 * メタデータを応答する
	 * @param meta
	 * @return
	 */
	private MetaTag convertToMetaTag(Metadata meta) {
		MetaTag metaTag = new MetaTag();
		for(Entry<String, Object> entry : meta.getMap(0).entrySet()) {
			// この方法だとwidthとかheightがdoubleでとれていればいいけど、ちがったら困る。
			// 文字列化してからparseDoubleする必要があるかも？
			// →試して見たところ問題なさそう
			
			// データのwidthとheightを取りたい場合はIVideoDataのwidthとheightを変更したほうがいいと思う。
			metaTag.putData(entry.getKey(), entry.getValue());
		}
		return metaTag;
	}
	/**
	 * メタデータを応答する。(ベースがMetadataAmf3バージョン)
	 * @param meta
	 * @return
	 */
	private MetaTag convertToMetaTag(MetadataAmf3 meta) {
		MetaTag metaTag = new MetaTag();
		for(Entry<String, Object> entry : meta.getData().entrySet()) {
			metaTag.putData(entry.getKey(), entry.getValue());
		}
		return metaTag;
	}
	/**
	 * Videoデータからvideo用のタグに書き換えます。
	 * @param video
	 * @return 変換できないもしくは変換する必要がないデータの場合はnullを応答する。
	 */
	private VideoTag convertToVideoTag(Video video) {
		RtmpHeader header = video.getHeader();
		ChannelBuffer data = video.encode();
		if(data.capacity() == 0) {
			return null;
		}
		VideoTag tag = new VideoTag();
		// 1バイト目を確認して種類等を確定しておく。
		byte tagByte = data.readByte();
		if(tag.analyzeTagByte(tagByte)) {
			// mshが必要な場合
			boolean mshFlg = data.readByte() == 0x00;
			tag.setMSHFlg(mshFlg);
		}
		if(data.readableBytes() == 0) {
			// 実態がない場合は処理してもしかたないので、捨てる
			return null;
		}
		tag.setTimestamp(header.getTime());
		ByteBuffer buffer = ByteBuffer.allocate(data.readableBytes());
		buffer.put(data.toByteBuffer());
		buffer.flip();
		tag.setRawData(buffer);
		return tag;
	}
	private VideoTag convertToVideoTag(RtmpHeader header, ChannelBuffer data) {
		if(!header.isVideo() || data.capacity() == 0) {
			return null;
		}
		VideoTag tag = new VideoTag();
		byte tagByte = data.readByte();
		if(tag.analyzeTagByte(tagByte)) {
			// mshが必要な動作次の1バイトも読み込む必要あり。
			boolean mshFlg = data.readByte() == 0x00;
			tag.setMSHFlg(mshFlg);
		}
		if(data.readableBytes() == 0) {
			return null;
		}
		tag.setTimestamp(header.getTime());
		// toByteBufferを実行すると残っているデータの分だけbyteBufferになるっぽい。
		// 仕方ないので別のByteBufferをつくって、一部だけのデータをつくっておく。
		ByteBuffer buffer = ByteBuffer.allocate(data.readableBytes());
		buffer.put(data.toByteBuffer());
		buffer.flip();
		tag.setRawData(buffer);
		return tag;
	}
	/**
	 * flvAtomからaudio用のタグに書き換えます。
	 * @param audio
	 * @return
	 */
	private AudioTag convertToAudioTag(Audio audio) {
		RtmpHeader header = audio.getHeader();
		ChannelBuffer data = audio.encode();
		if(data.capacity() == 0) {
			return null;
		}
		AudioTag tag = new AudioTag();
		byte tagByte = data.readByte();
		if(tag.analyzeTagByte(tagByte)) {
			boolean mshFlg = data.readByte() == 0x00;
			tag.setMSHFlg(mshFlg);
		}
		if(data.readableBytes() == 0) {
			return null;
		}
		tag.setTimestamp(header.getTime());
		ByteBuffer buffer = ByteBuffer.allocate(data.readableBytes());
		buffer.put(data.toByteBuffer());
		buffer.flip();
		tag.setRawData(buffer);
		return tag;
	}
	private AudioTag convertToAudioTag(RtmpHeader header, ChannelBuffer data) {
		if(!header.isAudio() || data.capacity() == 0) {
			return null;
		}
		AudioTag tag = new AudioTag();
		byte tagByte = data.readByte();
		if(tag.analyzeTagByte(tagByte)) {
			boolean mshFlg = data.readByte() == 0x00;
			tag.setMSHFlg(mshFlg);
		}
		if(data.readableBytes() == 0) {
			return null;
		}
		tag.setTimestamp(header.getTime());
		// toByteBuffer以下videoのやつと同じ
		ByteBuffer buffer = ByteBuffer.allocate(data.readableBytes());
		buffer.put(data.toByteBuffer());
		buffer.flip();
		tag.setRawData(buffer);
		return tag;
	}
}
