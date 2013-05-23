package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

public class Stss extends Atom {
	private byte version;
	private int flags;
	private int syncCount;
	public Stss(int size, int position) {
		super(Stss.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		int head = buffer.getInt();
		version = (byte)((head >> 24) & 0xFF);
		flags = (head & 0x00FFFFFF);
		syncCount = buffer.getInt();
		analyzed();
		// このあとはint値のkeyFrameになるsampleの番号値リスト
	}
	public byte getVersion() {
		return version;
	}
	public int getFlags() {
		return flags;
	}
	public int getSyncCount() {
		return syncCount;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
