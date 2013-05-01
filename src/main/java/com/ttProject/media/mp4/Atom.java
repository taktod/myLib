package com.ttProject.media.mp4;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

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
	public abstract void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception;
	public void analyze(IFileReadChannel ch) throws Exception {
		analyze(ch, null);
	}
	// そのままコピーする動作も必要
	public void copy(IFileReadChannel ch, WritableByteChannel target) throws Exception {
		// コピーするデータ量をなんとかしなければいけないと思う。
		// 開始位置をいれておく
		ch.position(getPosition());
		// ここからsize分読み込んで記入していく。
		ByteBuffer buffer = null;
		int targetSize = getSize();
		while(targetSize > 0) {
			// 結構な速度で動作可能っぽいです。(光回線だからですかね)
			int size = (167772160 > targetSize) ? targetSize : 167772160;
			buffer = ByteBuffer.allocate(size);
			ch.read(buffer);
			buffer.flip();
			if(buffer.remaining() == 0) {
				break;
			}
			targetSize -= buffer.remaining();
			target.write(buffer);
		}
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
