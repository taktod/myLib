package com.ttProject.flazr;

import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;

import com.flazr.io.flv.FlvAtom;
import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.Metadata;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AggregateTag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.media.flv.tag.VideoTag;

/**
 * rtmpメッセージを操作する動作
 * @author taktod
 */
public class RtmpMessageUtil {
	public Tag convertToTag(RtmpMessage message) {
		RtmpHeader header = message.getHeader();
		if(header.isAggregate()) {
			// tagのリストとして応答してやる必要がある。
			return convertToAggregateTag(message);
		}
		else if(header.isAudio()) {
			return convertToAudioTag(new FlvAtom(header.getMessageType(), header.getTime(), message.encode()));
		}
		else if(header.isVideo()) {
			return convertToVideoTag(new FlvAtom(header.getMessageType(), header.getTime(), message.encode()));
		}
		else if(header.isMetadata() && message instanceof Metadata) {
			return convertToMetaTag((Metadata)message);
		}
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
			final FlvAtom flvAtom = new FlvAtom(in);
			final RtmpHeader subHeader = flvAtom.getHeader();
			if(difference == -1) {
				difference = subHeader.getTime() - header.getTime();
			}
			subHeader.setTime(subHeader.getTime() - difference);
			Tag tag = null;
			if(subHeader.isAudio()) {
				tag = convertToAudioTag(flvAtom);
			}
			else if(subHeader.isVideo()) {
				tag = convertToVideoTag(flvAtom);
			}
			if(tag != null) {
				// データがある場合は追加しておく
				aTag.add(tag);
			}
		}
		if(aTag.getSize() == 0) {
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
			metaTag.putData(entry.getKey(), entry.getValue());
		}
		return metaTag;
	}
	/**
	 * flvAtomからvideo用のタグに書き換えます。
	 * @param atom
	 * @return
	 */
	private VideoTag convertToVideoTag(FlvAtom atom) {
		RtmpHeader header = atom.getHeader();
		ChannelBuffer data = atom.getData().duplicate();
		if(!header.isVideo() || data.capacity() == 0) { // データに問題がある場合は動作しない
			return null;
		}
		VideoTag tag = new VideoTag();
		byte tagByte = data.readByte();
		if(tag.analyzeTagByte(tagByte)) {
			// mshが必要な動作次の1バイトも読み込む必要あり。
			byte mshFlg = data.readByte();
			tag.setMSHFlg(mshFlg == 0x00);
		}
		tag.setTimestamp(header.getTime());
		tag.setData(data.toByteBuffer()); // toByteBufferを実行すると残っているデータの分だけbyteBufferになるっぽい。
		return tag;
	}
	/**
	 * flvAtomからaudio用のタグに書き換えます。
	 * @param atom
	 * @return
	 */
	private AudioTag convertToAudioTag(FlvAtom atom) {
		RtmpHeader header = atom.getHeader();
		ChannelBuffer data = atom.getData().duplicate();
		if(!header.isAudio() || data.capacity() == 0) {
			return null;
		}
		AudioTag tag = new AudioTag();
		byte tagByte = data.readByte();
		if(tag.analyzeTagByte(tagByte)) {
			byte mshFlg = data.readByte();
			tag.setMSHFlg(mshFlg == 0x00);
		}
		tag.setTimestamp(header.getTime());
		tag.setData(data.toByteBuffer());
		return tag;
	}
}
