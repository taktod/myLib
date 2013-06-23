package com.ttProject.media.mkv;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * matroskaのデータエレメントのベース
 * タグ + データの長さ + データ(データ長分はいっている)
 * @author taktod
 *
 */
public abstract class Element {
	private final Type type;
	private final long size;
	private final long position;
	private final long dataPosition;
	public Element(final Type type, final long position, final long size, final long dataPosition) {
		this.type = type;
		this.position = position;
		this.size = size;
		this.dataPosition = dataPosition;
	}
	public abstract void analyze(IFileReadChannel ch, IElementAnalyzer analyzer) throws Exception;
	public void analyze(IFileReadChannel ch) throws Exception {
		analyze(ch, null);
	}
	protected Type getType() {
		return type;
	}
	public long getSize() {
		return size;
	}
	public long getPosition() {
		return position;
	}
	public long getDataPosition() {
		return dataPosition;
	}
	@Override
	public String toString() {
		return toString("");
	}
	public String toString(String space) {
		StringBuilder data = new StringBuilder(space);
		data.append(type);
		data.append("[size:0x").append(Long.toHexString(size + dataPosition - position)).append("]");
		data.append("[position:0x").append(Long.toHexString(position)).append("]");
		return data.toString();
	}
	private static long getData(IFileReadChannel source, boolean removeBitflg) throws Exception {
		// はじめの1バイト目を確認して、先頭のbitがどこにあるか確認する。
		long result = BufferUtil.safeRead(source, 1).get() & 0xFF;
		// bitフラグがどこであるか確認する。
		int i = 0;
		for(i = 0;i < 8;i ++) {
			if((result & 0x80) != 0) {
				if(removeBitflg) {
					result &= 0x7F;
				}
				result >>= i;
				break;
			}
			result <<= 1;
		}
		// 必要な読み込みバイトはiにはいっている。
		if(i != 0) {
			ByteBuffer buffer = BufferUtil.safeRead(source, i);
			while(buffer.remaining() != 0) {
				result = result * 0x0100 + (buffer.get() & 0xFF);
			}
		}
		return result;
	}
	public static long getSize(IFileReadChannel source) throws Exception {
		return getData(source, true);
	}
	public static Type getTag(IFileReadChannel source) throws Exception {
		return Type.getType((int)getData(source, false));
	}
}
