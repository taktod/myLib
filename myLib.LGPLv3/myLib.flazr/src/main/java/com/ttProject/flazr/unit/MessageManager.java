/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.unit;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.Audio;
import com.flazr.rtmp.message.Metadata;
import com.flazr.rtmp.message.Video;
import com.ttProject.container.flv.AggregateTag;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.MetaTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.flazr.rtmp.message.MetadataAmf3;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * messageManager
 * to get flvTag on myLib.container.flv from flvAtom.
 * @author taktod
 * note this program try to make byteStream for flvTag.
 * it is possible to get frame directly.
 */
public class MessageManager {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(MessageManager.class);
	/** byteStream reader for flvTag */
	private FlvTagReader flvTagReader = new FlvTagReader();
	/**
	 * get flvTag from rtmpMessage.
	 * @param message
	 * @return
	 */
	public FlvTag getTag(RtmpMessage message) throws Exception {
		RtmpHeader header = message.getHeader();
		if(header.isAggregate()) {
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
			return convertToMetaTag((Metadata) message);
		}
		return null;
	}
	/**
	 * from aggregate message
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private AggregateTag convertToAggregateTag(RtmpMessage message) throws Exception {
		final RtmpHeader header = message.getHeader();
		int difference = -1;
		final ChannelBuffer in = message.encode();
		AggregateTag aTag = new AggregateTag();
		BitConnector connector = new BitConnector();
		while(in.readable()) {
			Bit8  type         = new Bit8(in.readByte());
			Bit24 dataSize     = new Bit24(in.readMedium());
			Bit24 timestamp    = new Bit24(in.readMedium());
			Bit8  timestampExt = new Bit8(in.readByte());
			Bit24 streamId     = new Bit24(0);
			Bit32 prevTagSize  = new Bit32(11 + dataSize.get());
			ByteBuffer buffer = ByteBuffer.allocate(dataSize.get() + 15);
			int time = timestamp.get() | timestampExt.get() << 24;
			if(difference == -1) {
				difference = time - header.getTime();
			}
			time = time - difference;
			timestamp.set(time & 0x00FFFFFF);
			timestampExt.set((time >> 24) & 0xFF);
			in.skipBytes(3);
			buffer.put(connector.connect(type, dataSize,
					timestamp, timestampExt, streamId));
			buffer.put(in.readBytes(dataSize.get()).toByteBuffer());
			buffer.put(connector.connect(prevTagSize));
			buffer.flip();
			in.skipBytes(4);
			FlvTag tag = (FlvTag)flvTagReader.read(new ByteReadChannel(buffer));
			if(tag != null) {
				aTag.add(tag);
			}
		}
		if(aTag.count() == 0) {
			return null;
		}
		else {
			return aTag;
		}
	}
	/**
	 * get videoTag
	 * @param video
	 * @return
	 * @throws Exception
	 */
	private VideoTag convertToVideoTag(Video video) throws Exception {
		RtmpHeader header = video.getHeader();
		ChannelBuffer data = video.encode();
		if(data.capacity() < 3) {
			// data is empty, not to make flvTag.
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(header.getSize() + 15);
		Bit8  type         = new Bit8(0x09);
		Bit24 dataSize     = new Bit24(header.getSize());
		Bit24 timestamp    = new Bit24(header.getTime() & 0x00FFFFFF);
		Bit8  timestampExt = new Bit8((header.getTime() >>> 24) & 0xFF);
		Bit24 streamId     = new Bit24(0);
		Bit32 prevTagSize  = new Bit32(11 + header.getSize());
		BitConnector connector = new BitConnector();
		buffer.put(connector.connect(type, dataSize, timestamp, timestampExt, streamId));
		buffer.put(data.toByteBuffer());
		buffer.put(connector.connect(prevTagSize));
		buffer.flip();
		VideoTag tag = (VideoTag)flvTagReader.read(new ByteReadChannel(buffer));
		return tag;
	}
	/**
	 * get audioTag
	 * @param audio
	 * @return
	 * @throws Exception
	 */
	private AudioTag convertToAudioTag(Audio audio) throws Exception {
		RtmpHeader header = audio.getHeader();
		ChannelBuffer data = audio.encode();
		if(data.capacity() < 3) {
			// data is empty, not to make flvTag.
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(header.getSize() + 15);
		Bit8  type         = new Bit8(0x08);
		Bit24 dataSize     = new Bit24(header.getSize());
		Bit24 timestamp    = new Bit24(header.getTime() & 0x00FFFFFF);
		Bit8  timestampExt = new Bit8((header.getTime() >>> 24) & 0xFF);
		Bit24 streamId     = new Bit24(0);
		Bit32 prevTagSize  = new Bit32(11 + header.getSize());
		BitConnector connector = new BitConnector();
		buffer.put(connector.connect(type, dataSize, timestamp, timestampExt, streamId));
		buffer.put(data.toByteBuffer());
		buffer.put(connector.connect(prevTagSize));
		buffer.flip();
		AudioTag tag = (AudioTag)flvTagReader.read(new ByteReadChannel(buffer));
		return tag;
	}
	/**
	 * get metaTag
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
	 * get metaTag(amf3)
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
