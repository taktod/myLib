package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * chunkがfile上のどこからはじまっているか指定してあります。
 * @author taktod
 */
public class Stco extends Atom {
	private byte version;
	private int flags;
	private int offsetCount;
	
	private CacheBuffer buffer;
	private int chunkPos = -1;
	public Stco(int size, int position) {
		super(Stco.class.getSimpleName().toLowerCase(), size, position);
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
		// この後のデータはchunkの開始indexがならんでいるだけ
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
	public int nextChunkPos() throws Exception {
		if(buffer.remaining() == 0) {
			return -1;
		}
		chunkPos = buffer.getInt();
		return chunkPos;
	}
	public boolean hasMore() {
		return buffer.remaining() != 0;
	}
	public int getChunkPos() {
		return chunkPos;
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
