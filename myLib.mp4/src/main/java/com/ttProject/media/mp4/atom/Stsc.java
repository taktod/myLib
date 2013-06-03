package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.CustomBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 各チャンクが保持しているサンプル数を保持しています。
 * @author taktod
 *
 */
public class Stsc extends Atom {
	private byte version;
	private int flags;
	private int count;
	public Stsc(int size, int position) {
		super(Stsc.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		int head = buffer.getInt();
		version = (byte)((head >> 24) & 0xFF);
		flags = (head & 0x00FFFFFF);
		count = buffer.getInt();
		analyzed();
		// このあとのデータは開始chunk番号 含有サンプル数 データ参照indexとなっている。(すべてint)
		/*
		 * 1,3,2 4,2,1 6,1,2とある場合
		 * 1,3,2
		 * 2,3,2
		 * 3,3,2
		 * 4,2,1
		 * 5,2,1
		 * 6,1,2
		 * 7,1,2...となる
		 */
	}
	private IFileReadChannel source;
	private int nextChunkNum;
	private int nextSampleCount;
	private int nextDataRef;
	private int chunkNum;
	private int sampleCount;
	private int dataRef;
	private CustomBuffer buffer;
//	private int currentPos;
	public void start(IFileReadChannel src, boolean copy) throws Exception {
		if(copy) {
			source = FileReadChannel.openFileReadChannel(src.getUri());
		}
		else {
			source = src;
		}
		source.position(getPosition() + 16);
		buffer = new CustomBuffer(source, getSize() - 16);
//		currentPos = source.position();
	}
	public int nextChunk() throws Exception {
		chunkNum ++;
		if(nextChunkNum > chunkNum) {
			return nextChunkNum;
		}
		else if(chunkNum == nextChunkNum) {
			sampleCount = nextSampleCount;
			dataRef = nextDataRef;
		}
		if(buffer.remaining() == 0) {
			sampleCount = 1;
			if(chunkNum == nextChunkNum) {
				return chunkNum;
			}
			// まだデータがのこっている場合はそれを応答する。
			return -1;
		}
//		source.position(currentPos);
//		ByteBuffer buffer = BufferUtil.safeRead(source, 12);
//		currentPos = source.position();
		sampleCount = nextSampleCount;
		dataRef = nextDataRef;
		nextChunkNum = buffer.getInt();
		nextSampleCount = buffer.getInt();
		nextDataRef = buffer.getInt();
		if(chunkNum == nextChunkNum) {
			sampleCount = nextSampleCount;
			dataRef = nextDataRef;
		}
		return nextChunkNum;
	}
/*	public void start(IFileReadChannel src, boolean copy) throws Exception {
		if(copy) {
			source = FileReadChannel.openFileReadChannel(src.getUri());
		}
		else {
			source = src;
		}
		source.position(getPosition() + 16);
		currentPos = source.position();
	}
	public int nextChunk() throws Exception {
		chunkNum ++;
		if(nextChunkNum > chunkNum) {
			return nextChunkNum;
		}
		else if(chunkNum == nextChunkNum) {
			sampleCount = nextSampleCount;
			dataRef = nextDataRef;
		}
		if(currentPos == getPosition() + getSize()) {
			sampleCount = 1;
			if(chunkNum == nextChunkNum) {
				return chunkNum;
			}
			// まだデータがのこっている場合はそれを応答する。
			return -1;
		}
		source.position(currentPos);
		ByteBuffer buffer = BufferUtil.safeRead(source, 12);
		currentPos = source.position();
		sampleCount = nextSampleCount;
		dataRef = nextDataRef;
		nextChunkNum = buffer.getInt();
		nextSampleCount = buffer.getInt();
		nextDataRef = buffer.getInt();
		if(chunkNum == nextChunkNum) {
			sampleCount = nextSampleCount;
			dataRef = nextDataRef;
		}
		return nextChunkNum;
	}// */
	public int getChunkNum() {
		return chunkNum;
	}
	public int getSampleCount() {
		return sampleCount;
	}
	public int getDataRef() {
		return dataRef;
	}
	public byte getVersion() {
		return version;
	}
	public int getFlags() {
		return flags;
	}
	public int getCount() {
		return count;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
