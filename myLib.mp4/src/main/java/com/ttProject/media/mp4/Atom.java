package com.ttProject.media.mp4;

import java.nio.channels.WritableByteChannel;

import com.ttProject.util.BufferUtil;
import com.ttProject.nio.channels.IReadChannel;

/**
 * atomデータ
 * @author taktod
 */
public abstract class Atom {
	/** データサイズ */
	private final int size;
	/** atom名 */
	private final String name;
	/** ファイル上の開始位置 */
	private final int position;
	/** 解析済みフラグ */
	private boolean analized;
	/** 先頭の1バイトに入っているversion */
	private byte version;
	/** 続く3バイトのフラグ情報 */
	private int flags;
	/**
	 * コンストラクタ
	 * @param name
	 * @param size
	 * @param position
	 */
	public Atom(final String name, final int size, final int position) {
		this.size = size;
		this.name = name;
		this.position = position;
		this.analized = false;
	}
	/**
	 * 解析動作
	 * @param ch
	 * @param analyzer
	 * @throws Exception
	 */
	public abstract void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception;
	/**
	 * 解析動作
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IReadChannel ch) throws Exception {
		analyze(ch, null);
	}
	/**
	 * データコピー
	 * @param ch
	 * @param target
	 * @throws Exception
	 */
	public void copy(IReadChannel ch, WritableByteChannel target) throws Exception {
		// 開始位置をいれておく
		ch.position(getPosition());
		BufferUtil.quickCopy(ch, target, getSize());
	}
	/**
	 * サイズ参照
	 * @return
	 */
	public int getSize() {
		return size;
	}
	/**
	 * atom名参照
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * オリジナルファイルatom開始位置参照
	 * @return
	 */
	public int getPosition() {
		return position;
	}
	/**
	 * 解析済みかどうか？
	 * @return
	 */
	public boolean isAnalyzed() {
		return analized;
	}
	/**
	 * 解析したフラグ更新
	 */
	public void analyzed() {
		this.analized = true;
	}
	/**
	 * tagのあとにあるversionとflagsについて解析しておく。
	 * @param data
	 */
	protected void analyzeFirstInt(int data) {
		version = (byte)((data >> 24) & 0xFF);
		flags = (data & 0x00FFFFFF);
	}
	/**
	 * version参照
	 * @return
	 */
	public byte getVersion() {
		return version;
	}
	/**
	 * flags参照
	 * @return
	 */
	public int getFlags() {
		return flags;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toString("");
	}
	/**
	 * 情報表示
	 * @param space
	 * @return
	 */
	public String toString(String space) {
		StringBuilder data = new StringBuilder(space);
		data.append(name);
		data.append("[size:0x").append(Integer.toHexString(size)).append("]");
		data.append("[position:0x").append(Integer.toHexString(position)).append("]");
		return data.toString();
	}
}
