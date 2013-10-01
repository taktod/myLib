package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * stcoの64bitバージョン
 * chunkがfile上のどこにあるか保持しています
 * @author taktod
 */
public class Ctts extends Atom {
	private int offsetCount;
	public Ctts(int position, int size) {
		super(Ctts.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		analyzeFirstInt(buffer.getInt());
		offsetCount = buffer.getInt();
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
	public int getOffsetCount() {
		return offsetCount;
	}
}
