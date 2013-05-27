package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

public class Mdhd extends Atom {
	private byte version;
	private int flags;
	private long creationTime;
	private long modifitaionTime;
	private int timescale;
	private long duration;
	public Mdhd(int size, int position) {
		super(Mdhd.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, getSize() - 8);
		int head = buffer.getInt();
		version = (byte)((head >> 24) & 0xFF);
		flags = (head & 0x00FFFFFF);
		if(version == 0) {
			creationTime = buffer.getInt();
			modifitaionTime = buffer.getInt();
		}
		else {
			creationTime = buffer.getLong();
			modifitaionTime = buffer.getLong();
		}
		timescale = buffer.getInt();
		if(version == 0) {
			duration = buffer.getInt();
		}
		else {
			duration = buffer.getLong();
		}
		// あとはpad 1 とLanguage 5x3 Reserved 16 = 32bit でおわるはず。
		// 4バイトのこっているはず。
	}
	public int getTimescale() {
		return timescale;
	}
	public long getDuration() {
		return duration;
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
