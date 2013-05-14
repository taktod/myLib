package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * stcoの64bitバージョン
 * chunkがfile上のどこにあるか保持しています
 * @author taktod
 */
public class Co64 extends Atom {
	private byte version;
	private int flags;
	private int offsetCount;
	public Co64(int size, int position) {
		super(Co64.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		int head = buffer.getInt();
		version = (byte)((head >> 24) & 0xFF);
		flags = (head & 0x00FFFFFF);
		offsetCount = buffer.getInt();
		analyzed();
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
	public byte getVersion() {
		return version;
	}
	public int getFlags() {
		return flags;
	}
	public int getOffsetCount() {
		return offsetCount;
	}
}
