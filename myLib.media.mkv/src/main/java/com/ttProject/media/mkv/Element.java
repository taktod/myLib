package com.ttProject.media.mkv;

import java.nio.ByteBuffer;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * matroskaのデータのエレメントベース
 * @author taktod
 * longであつかうか迷うところ・・・どうしようかのぉ・・・
 */
public abstract class Element extends Unit {
	private final Type type;
	private long size;
	private long position;
	private long dataPosition;
	/**
	 * コンストラクタ(仮)
	 */
	public Element(Type type, long position, long size, long dataPosition) {
		super(0, 0);
		this.type = type;
		this.position = position;
		this.size = size;
		this.dataPosition = dataPosition;
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toString("");
	}
	/**
	 * データを参照する
	 * @param space
	 * @return
	 */
	public String toString(String space) {
		StringBuilder data = new StringBuilder(space);
		data.append(type);
		data.append("[size:0x").append(Long.toHexString(size + dataPosition - position)).append("]");
		data.append("[position:0x").append(Long.toHexString(position)).append("]");
		return data.toString();
	}
	/**
	 * ebmlデータを扱うための汎用処理
	 * @param source
	 * @param removeBitflg
	 * @return
	 * @throws Exception
	 */
	private static long getData(IReadChannel source, boolean removeBitflg) throws Exception {
		long result = BufferUtil.safeRead(source, 1).get() & 0xFF;
		int i = 0;
		for(i = 0;i < 8;i ++) {
			if((result & 0x80) != 0) {
				if(removeBitflg) {
					result &= 0x7F;
				}
				result >>= i;
			}
			result <<= 1;
		}
		if(i != 0) {
			ByteBuffer buffer = BufferUtil.safeRead(source, i);
			while(buffer.remaining() != 0) {
				result = (result << 8) + (buffer.get() & 0xFF);
			}
		}
		return result;
	}
	public static long getSize(IReadChannel source) throws Exception {
		return getData(source, true);
	}
	public static long getEbmlNumber(IReadChannel source) throws Exception {
		return getData(source, true);
	}
	public static Type getType(IReadChannel source) throws Exception {
		return Type.getType(((int)getData(source, false)));
	}
}
