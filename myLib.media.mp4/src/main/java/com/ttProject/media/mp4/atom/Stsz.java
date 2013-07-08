package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 各サンプルのデータサイズを保持してあります。
 * @author taktod
 */
public class Stsz extends Atom {
	private int constSize;
	private int sizeCount; // このデータがsample数と同値になります。

	private CacheBuffer buffer;
	private int sampleSize;
	public Stsz(int position, int size) {
		super(Stsz.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 12);
		analyzeFirstInt(buffer.getInt());
		constSize = buffer.getInt();
		sizeCount = buffer.getInt();
		// このあとのデータは各サンプルのサイズになります。
		// ただしconstSizeの場合は存在しません。
	}
	public void start(IReadChannel src, boolean copy) throws Exception {
		IReadChannel source;
		if(copy) {
			if(!(src instanceof IFileReadChannel)) {
				throw new Exception("IFileReadChannel系のreadChannelでないと、オブジェクトのcloneは作成不能です");
			}
			source = FileReadChannel.openFileReadChannel(((IFileReadChannel)src).getUri());
		}
		else {
			source = src;
		}
		source.position(getPosition() + 20);
		buffer = new CacheBuffer(source, getSize() - 20);
	}
	public int nextSampleSize() throws Exception {
		if(buffer.remaining() == 0) {
			return -1;
		}
		sampleSize = buffer.getInt();
		return sampleSize;
	}
	public int getSampleSize() {
		return sampleSize;
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
