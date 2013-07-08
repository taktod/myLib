package com.ttProject.media.mp4;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.nio.channels.IReadChannel;

public abstract class Atom extends Unit {
	/** atom名 */
	private final String name;
	/** 先頭の1バイトに入っているversion */
	private byte version;
	/** 続く3バイトのフラグ情報 */
	private int flags;
	/**
	 * コンストラクタ
	 * @param name
	 * @param position
	 * @param size
	 */
	public Atom(String name, int position, int size) {
		super(position, size);
		this.name = name;
	}
	/**
	 * atom名参照
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * tagのあとに大抵あるversionとflagの解析
	 * @param data
	 */
	protected void analyzeFirstInt(int data) {
		version = (byte)((data >>> 24) & 0xFF);
		flags = (data & 0x00FFFFFF);
	}
	/**
	 * version値
	 * @return
	 */
	public byte getVersion() {
		return version;
	}
	/**
	 * flags値
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
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
		if(analyzer != null && !(analyzer instanceof IAtomAnalyzer)) {
			throw new Exception("Atomの解析用のanalyzerではないです。");
		}
		this.analyze(ch, (IAtomAnalyzer)analyzer);
	}
	/**
	 * atomの解析動作
	 * @param ch
	 * @param analyzer
	 */
	public abstract void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception;
	/**
	 * 情報表示
	 * @param space
	 * @return
	 */
	public String toString(String space) {
		StringBuilder data = new StringBuilder(space);
		data.append(name);
		data.append("[size:0x").append(Integer.toHexString(getSize())).append("]");
		data.append("[pos:0x").append(Integer.toHexString(getPosition())).append("]");
		return data.toString();
	}
}
