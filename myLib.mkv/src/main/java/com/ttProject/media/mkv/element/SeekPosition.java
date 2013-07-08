package com.ttProject.media.mkv.element;

import java.nio.ByteBuffer;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 必要な要素がどこにあるか応答を返してくれるみたいです。
 * ただ、内部のposのデータはsegmentのdataPositionからの位置みたいです。
 * @author taktod
 *
 */
public class SeekPosition extends Element {
	private int pos;
	public SeekPosition(long position, long size, long dataPosition) {
		super(Type.SeekPosition, position, size, dataPosition);
	}
	public SeekPosition(IReadChannel ch) throws Exception {
		this(ch.position() - Type.SeekPosition.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
		ch.position((int)getDataPosition());
		ByteBuffer buffer = BufferUtil.safeRead(ch, (int)getSize());
		pos = 0;
		while(buffer.remaining() != 0) {
			pos = pos * 0x0100 + (buffer.get() & 0xFF);
		}
		System.out.println(Integer.toHexString(pos));
	}
	@Override
	public String toString() {
		return super.toString("      ") + "[posData:0x" + Integer.toHexString(pos) + "]";
	}
}
