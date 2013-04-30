package com.ttProject.media.mp4;

import com.ttProject.nio.channels.IFileReadChannel;

public abstract class Atom {
	private final int size; // サイズ
	private final String name; // 名前
	private final int position; // file上のデータの開始位置
	private boolean analized;
	public Atom(final String name, final int size, final int position) {
		this.size = size;
		this.name = name;
		this.position = position;
		this.analized = false;
	}
	// 開始の読み込み位置については、先頭になるように外部で調整しておく必要あり。(と定めるURLConnectionの件もあるため。)
	public abstract void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception;
	public void analyze(IFileReadChannel ch) throws Exception {
		analyze(ch, null);
	}

	public int getSize() {
		return size;
	}
	public String getName() {
		return name;
	}
	public int getPosition() {
		return position;
	}
	public boolean isAnalyzed() {
		return analized;
	}
	public void analyzed() {
		this.analized = true;
	}
	@Override
	public String toString() {
		return toString("");
	}
	public String toString(String space) {
		StringBuilder data = new StringBuilder(space);
		data.append(name);
		data.append("[size:0x").append(Integer.toHexString(size)).append("]");
		data.append("[position:0x").append(Integer.toHexString(position)).append("]");
		return data.toString();
	}
}
