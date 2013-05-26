package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 各サンプルのデータサイズを保持してあります。
 * @author taktod
 */
public class Stsz extends Atom {
	private byte version;
	private int flags;
	private int constSize;
	private int sizeCount; // このデータがsample数と同値になります。
	public Stsz(int size, int position) {
		super(Stsz.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 12);
		int head = buffer.getInt();
		version = (byte)((head >> 24) & 0xFF);
		flags = (head & 0x00FFFFFF);
		constSize = buffer.getInt();
		sizeCount = buffer.getInt();
		analyzed();
		// このあとのデータは各サンプルのサイズになります。
		// ただしconstSizeの場合は存在しません。
	}
	private IFileReadChannel source;
	private int sampleSize;
	private int currentPos;
	public void start(IFileReadChannel src, boolean copy) throws Exception {
		if(copy) {
			source = FileReadChannel.openFileReadChannel(src.getUri());
		}
		else {
			source = src;
		}
		source.position(getPosition() + 20);
		currentPos = source.position();
	}
	public int nextSampleSize() throws Exception {
		if(currentPos == getPosition() + getSize()) {
			return -1;
		}
		source.position(currentPos);
		ByteBuffer buffer = BufferUtil.safeRead(source, 4);
		currentPos = source.position();
		sampleSize = buffer.getInt();
		return sampleSize;
	}
	public int getSampleSize() {
		return sampleSize;
	}
	public byte getVersion() {
		return version;
	}
	public int getFlags() {
		return flags;
	}
	public int getConstSize() {
		return constSize;
	}
	public int getSizeCount() {
		return sizeCount;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
