package com.ttProject.media.flv;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvデータのタグ
 * @author taktod
 */
public abstract class Tag extends Unit {
	/** timestamp */
	private int timestamp;
	/**
	 * コンストラクタ
	 */
	public Tag() {
		super(0, 0);
	}
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 * @param timestamp
	 */
	public Tag(final int position, final int size, final int timestamp) {
		// ここで保持しているサイズはタグの中身のサイズになっているので、調整が必要。
		super(position, size);
		this.timestamp = timestamp;
	}
	/**
	 * 単にそのままコピーする動作
	 * @param ch
	 * @param target
	 * @throws Exception
	 */
	@Deprecated
	public void copy(IFileReadChannel ch, WritableByteChannel target) throws Exception {
		int position = ch.position();
		// 開始位置をいれておく
		ch.position(getPosition());
		BufferUtil.quickCopy(ch, target, getSize() + 11 + 4);
		ch.position(position);
	}
	/**
	 * 元のファイルから読み込んで解析しておく。
	 * @param ch 読み込みデータソース
	 * @param atBegin true:初めから読み込む false:実データ部から読み込む
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public void analyze(IFileReadChannel ch, boolean atBegin) throws Exception {
		// ファイルから読み込んでなんとかしておく。
		if(atBegin) {
			// 先頭部分のデータを読み込んでおく。
			ch.position(getPosition());
			// サイズとか読み込んで利用しておく
			ByteBuffer buffer = BufferUtil.safeRead(ch, 11);
			byte type = buffer.get();
			int innerSize = analyzeInnerSize(buffer);
			int timestasmp = analyzeTimestamp(buffer);
			int trackId = analyzeTrackId(buffer);
		}
	}
	public void analyze(IFileReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
		analyze(ch, true);
	}
	/**
	 * 解析を実施する(初めから読み込まないバージョン)
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IFileReadChannel ch) throws Exception {
		analyze(ch, true);
	}
	/**
	 * 実際のflvのデータサイズ
	 * @return
	 */
//	public abstract int getRealSize() throws Exception;
	/**
	 * 書き出しを実行します
	 * @param target
	 * @throws Exception
	 */
	public abstract void writeTag(WritableByteChannel target) throws Exception;
	/**
	 * 内部データをByteBufferの形で取り出します
	 */
	public abstract ByteBuffer getBuffer() throws Exception;
	/**
	 * タイムスタンプ参照
	 * @return
	 */
	public int getTimestamp() {
		return timestamp;
	}
	/**
	 * タイムスタンプ更新
	 * @param timestamp
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * headerBufferの作成補助
	 * @param type
	 * @return
	 */
	protected ByteBuffer getHeaderBuffer(byte type) {
		ByteBuffer buffer = ByteBuffer.allocate(11);
		buffer.put(type);
		buffer.put(getSizeBytes());
		buffer.put(getTimestampBytes());
		buffer.put(getTrackBytes());
		buffer.flip();
		return buffer;
	}
	/**
	 * 終端Bufferの作成補助
	 * @return
	 */
	protected ByteBuffer getTailBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(getSize() - 4);
		buffer.flip();
		return buffer;
	}
	/**
	 * サイズBufferの作成補助
	 * @return
	 */
	protected byte[] getSizeBytes() {
		int size = getSize() - 15;
		return new byte[] {
				(byte)((size >> 16) & 0xFF),
				(byte)((size >> 8) & 0xFF),
				(byte)((size >> 0) & 0xFF),
		};
	}
	/**
	 * timestampBufferの作成補助
	 * @return
	 */
	protected byte[] getTimestampBytes() {
		return new byte[]{
				(byte)((timestamp >> 16) & 0xFF),
				(byte)((timestamp >> 8) & 0xFF),
				(byte)((timestamp >> 0) & 0xFF),
				(byte)((timestamp >> 24) & 0xFF)
		};
	}
	/**
	 * trackIDBufferの作成補助(0固定)
	 * @return
	 */
	protected byte[] getTrackBytes() {
		return new byte[] {
				(byte)0x00,
				(byte)0x00,
				(byte)0x00
		};
	}
	/**
	 * 終端タグ用サイズ作成補助
	 * @return
	 */
	protected byte[] eofSizeBytes() {
		int eofSize = getSize() - 4;
		return new byte[] {
				(byte)((eofSize >> 24) & 0xFF),
				(byte)((eofSize >> 16) & 0xFF),
				(byte)((eofSize >> 8) & 0xFF),
				(byte)((eofSize >> 0) & 0xFF)
		};
	}
	private static int analyzeInnerSize(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) + ((buffer.get() & 0xFF) << 8) + (buffer.get() & 0xFF);
	}
	private static int analyzeTimestamp(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) + ((buffer.get() & 0xFF) << 8) + (buffer.get() & 0xFF) + ((buffer.get() & 0xFF) << 24);
	}
	private static int analyzeTrackId(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) + ((buffer.get() & 0xFF) << 8) + (buffer.get() & 0xFF);
	}
	/**
	 * タグを読み込んで解析しておく。
	 * @param source
	 * @return
	 */
	public static Tag getTag(IFileReadChannel source) throws Exception {
		// この部分はTagとFlvHeaderのどちらかを応答するので、Unitを応答するものとします。s
		// 通常データを読み込みます。
		if(source.size() - source.position() < 11) {
			return null;
		}
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 11);
		byte type = buffer.get();
		int innerSize = analyzeInnerSize(buffer);
		int timestamp = analyzeTimestamp(buffer);
		@SuppressWarnings("unused")
		int trackId = analyzeTrackId(buffer);
		switch(type) {
		case 0x08:
			return new AudioTag(position, innerSize + 15, timestamp);
		case 0x09:
			return new VideoTag(position, innerSize + 15, timestamp);
		case 0x12:
			return new MetaTag(position, innerSize + 15, timestamp);
		default:
			throw new Exception("解析不能なflvデータタグをうけとりました。:" + Integer.toHexString(type));
		}
	}
}
