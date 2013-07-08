package com.ttProject.media.flv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvデータを管理します
 * @author taktod
 */
public class FlvManager extends Manager<Tag> {
	/** データタイプ */
	private enum Type {
		Audio(0x08),
		Video(0x09),
		Meta(0x12),
		Unknown(-1);
		private final int value;
		private Type(int value) {
			this.value = value;
		}
		public int intValue() {
			return value;
		}
		public static Type getType(int value) throws Exception {
			for(Type t : values()) {
				if(t.intValue() == value) {
					return t;
				}
			}
			throw new Exception("解析不能なタグデータを受けとりました。" + Integer.toHexString(value));
		}
	};
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Tag> getUnits(ByteBuffer data) {
		ByteBuffer buffer = appendBuffer(data);
		if(buffer == null) {
			return null;
		}
		IReadChannel bufferChannel = new ByteReadChannel(buffer);
		List<Tag> result = new ArrayList<Tag>();
		try {
			while(true) {
				int position = bufferChannel.position();
				Tag tag = getUnit(bufferChannel);
				// tagを読み込むのに必要なデータ量があるか確認しておく。
				if(tag == null) {
					// データが足りない
					buffer.position(position);
					break;
				}
				tag.analyze(bufferChannel, false);
				result.add(tag);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tag getUnit(IReadChannel source) throws Exception {
		// データ量が11バイト以上ないと解析不能
		if(source.size() - source.position() < 11) {
			return null;
		}
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 11);
		Type type = Type.getType(buffer.get());
		int innerSize = analyzeInnerSize(buffer);
		int timestamp = analyzeTimestamp(buffer);
		@SuppressWarnings("unused")
		int trackId = analyzeTrackId(buffer);
		if(source.size() - source.position() < innerSize + 4) {
			// タグの終端まで必要なデータがない場合
			source.position(position);
			return null;
		}
		switch(type) {
		case Audio:
			return new AudioTag(position, innerSize + 15, timestamp);
		case Video:
			return new VideoTag(position, innerSize + 15, timestamp);
		case Meta:
			return new MetaTag(position, innerSize + 15, timestamp);
		default:
			throw new RuntimeException("解析不能なflvデータタグを受けとりました。");
		}
	}
	private int analyzeInnerSize(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) + ((buffer.get() & 0xFF) << 8) + (buffer.get() & 0xFF);
	}
	private int analyzeTimestamp(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) + ((buffer.get() & 0xFF) << 8) + (buffer.get() & 0xFF) + ((buffer.get() & 0xFF) << 24);
	}
	private int analyzeTrackId(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) + ((buffer.get() & 0xFF) << 8) + (buffer.get() & 0xFF);
	}
}
