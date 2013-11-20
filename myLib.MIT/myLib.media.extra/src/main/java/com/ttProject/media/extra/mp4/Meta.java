package com.ttProject.media.extra.mp4;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * metaデータ用のatom
 * @author taktod
 */
public class Meta extends Atom implements IIndexAtom {
	/** 動画データの高さ */
	private int height = 0;
	/** 動画データの幅 */
	private int width = 0;
	/** 再生長(ミリ秒単位) */
	private long duration = 0;
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 */
	public Meta(int position, int size) {
		super(Meta.class.getSimpleName().toLowerCase(), position, size);
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * 動画の長さ参照(ミリ秒)
	 * @return
	 */
	public long getDuration() {
		return duration;
	}
	/**
	 * 動画の長さ設定(ミリ秒)
	 * @param duration
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 20);
		buffer.position(4);
		width = buffer.getInt();
		height = buffer.getInt();
		duration = buffer.getLong();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeIndex(WritableByteChannel idx) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(28);
		buffer.putInt(getSize());
		buffer.put("meta".getBytes());
		buffer.putInt(0); // version + flags
		buffer.putInt(width);
		buffer.putInt(height);
		buffer.putLong(duration);
		buffer.flip();
		idx.write(buffer);
	}
	/**
	 * flv用のmetaTagを生成します。
	 * @return
	 */
	public MetaTag createFlvMetaTag() {
		MetaTag metaTag = new MetaTag();
		metaTag.setTimestamp(0);
		if(width != 0) {
			metaTag.putData("width", getWidth());
		}
		if(height != 0) {
			metaTag.putData("height", getHeight());
		}
		if(duration != 0) {
			metaTag.putData("duration", getDuration() / 1000.0D);
		}
		return metaTag;
	}
}
