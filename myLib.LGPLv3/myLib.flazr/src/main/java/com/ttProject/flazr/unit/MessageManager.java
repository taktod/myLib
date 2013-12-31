package com.ttProject.flazr.unit;

import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.Metadata;
import com.flazr.rtmp.message.Video;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.container.flv.type.MetaTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.VideoAnalyzer;

/**
 * flazrのflvAtomからmyLib.container.flvのflvTagを取り出す動作
 * @author taktod
 */
public class MessageManager {
	// こいつもframeAnalyzerを保持しておいて、どういうフレームがあるかまで解析させた方が幸せになれそう。
	private VideoAnalyzer videoFrameAnalyzer = null;
	private AudioAnalyzer audioFrameAnalyzer = null;
	/**
	 * rtmpメッセージからmyLib.container.flvのFlvTagを作成して応答します。
	 * @param message
	 * @return
	 */
	public FlvTag getTag(RtmpMessage message) throws Exception {
		RtmpHeader header = message.getHeader();
		if(header.isAggregate()) {
			
		}
		else if(header.isAudio()) {
			
		}
		else if(header.isVideo()) {
			
		}
		else if(message instanceof MetadataAmf3) {
			return convertToMetaTag((MetadataAmf3)message);
		}
		else if(header.isMetadata() && message instanceof Metadata) {
			return convertToMetaTag((Metadata) message);
		}
		return null;
	}
	/**
	 * 動画データに変換する。
	 * @param video
	 * @return
	 */
	private VideoTag convertToVideoTag(Video video) {
		RtmpHeader header = video.getHeader();
		ChannelBuffer data = video.encode();
		if(data.capacity() == 0) {
			return null;
		}
		VideoTag tag = new VideoTag();
		byte tagByte = data.readByte();
		return tag;
	}
	/**
	 * メタデータを応答します
	 * @param meta
	 * @return
	 */
	private MetaTag convertToMetaTag(Metadata meta) throws Exception {
		MetaTag metaTag = new MetaTag();
		for(Entry<String, Object> entry : meta.getMap(0).entrySet()) {
			metaTag.put(entry.getKey(), entry.getValue());
		}
		return metaTag;
	}
	/**
	 * メタデータを応答します
	 * @param meta
	 * @return
	 */
	private MetaTag convertToMetaTag(MetadataAmf3 meta) throws Exception {
		MetaTag metaTag = new MetaTag();
		for(Entry<String, Object> entry : meta.getData().entrySet()) {
			metaTag.put(entry.getKey(), entry.getValue());
		}
		return metaTag;
	}
}
