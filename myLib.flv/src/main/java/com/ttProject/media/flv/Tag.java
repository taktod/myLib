package com.ttProject.media.flv;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvデータのタグ
 * @author taktod
 */
public abstract class Tag {
	/** 中のデータのサイズ */
	private int size;
	/** 読み込みファイル上での元の位置 */
	private int position;
	/** timestamp */
	private int timestamp;
	/**
	 * コンストラクタ
	 */
	public Tag() {
		
	}
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 * @param timestamp
	 */
	public Tag(final int size, final int position, final int timestamp) {
		this.size = size;
		this.position = position;
		this.timestamp = timestamp;
	}
	/**
	 * 単にそのままコピーする動作
	 * @param ch
	 * @param target
	 * @throws Exception
	 */
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
	public abstract void analyze(IFileReadChannel ch, boolean atBegin) throws Exception;
	/**
	 * 解析を実施する(初めから読み込まないバージョン)
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IFileReadChannel ch) throws Exception {
		analyze(ch, false);
	}
	/**
	 * 書き出しを実行します
	 * @param target
	 * @throws Exception
	 */
	public abstract void writeTag(WritableByteChannel target) throws Exception;
	/**
	 * サイズデータの更新
	 * @param size
	 */
	protected void setSize(int size) {
		this.size = size;
	}
	/**
	 * サイズ参照
	 * @return
	 */
	public int getSize() {
		return size;
	}
	/**
	 * 位置参照
	 * @return
	 */
	public int getPosition() {
		return position;
	}
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
		buffer.put(getSizeBuffer());
		buffer.put(getTimestampBuffer());
		buffer.put(getTrackBuffer());
		buffer.flip();
		return buffer;
	}
	/**
	 * 終端Bufferの作成補助
	 * @return
	 */
	protected ByteBuffer getTailBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(size + 11);
		buffer.flip();
		return buffer;
	}
	/**
	 * サイズBufferの作成補助
	 * @return
	 */
	protected byte[] getSizeBuffer() {
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
	protected byte[] getTimestampBuffer() {
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
	protected byte[] getTrackBuffer() {
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
	protected byte[] eofSizeBuffer() {
		int eofSize = size + 11;
		return new byte[] {
				(byte)((eofSize >> 24) & 0xFF),
				(byte)((eofSize >> 16) & 0xFF),
				(byte)((eofSize >> 8) & 0xFF),
				(byte)((eofSize >> 0) & 0xFF)
		};
	}
}
