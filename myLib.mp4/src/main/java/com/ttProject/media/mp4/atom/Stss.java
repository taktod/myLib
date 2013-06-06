package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

public class Stss extends Atom {
	private int syncCount;

	private CacheBuffer buffer;
	private int keyFrame = -1;
	public Stss(int size, int position) {
		super(Stss.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		analyzeFirstInt(buffer.getInt());
		syncCount = buffer.getInt();
		analyzed();
		// このあとはint値のkeyFrameになるsampleの番号値リスト
	}
	public void start(IFileReadChannel src, boolean copy) throws Exception {
		IFileReadChannel source;
		if(copy) {
			source = FileReadChannel.openFileReadChannel(src.getUri());
		}
		else {
			source = src;
		}
		source.position(getPosition() + 16);
		buffer = new CacheBuffer(source, getSize() - 16);
	}
	public int nextKeyFrame() throws Exception {
		if(buffer.remaining() == 0) {
			return -1;
		}
		keyFrame = buffer.getInt();
		return keyFrame;
	}
	public int getKeyFrame() {
		return keyFrame;
	}
	public int getSyncCount() {
		return syncCount;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
