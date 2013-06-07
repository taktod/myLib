package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 各チャンクが保持しているサンプル数を保持しています。
 * @author taktod
 *
 */
public class Stsc extends Atom {
	private int count;

	private CacheBuffer buffer;
	private int nextChunkNum	= 0;
	private int nextSampleCount	= 0;
	private int nextDataRef		= 0;
	private int chunkNum;
	private int sampleCount;
	private int dataRef;
	public Stsc(int size, int position) {
		super(Stsc.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		analyzeFirstInt(buffer.getInt());
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
		 * 一番最後のデータのあとはおわりまで同じデータがなんども繰り返される
		 * よってstcoの値がわからないと最後がどこであるか判定できない。(ずっと同じあたいがかえってくるので注意が必要)
		 */
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
	public int nextChunk() throws Exception {
		chunkNum ++;
		if(nextChunkNum > chunkNum) {
			return nextChunkNum; // 一番最後のchunkNumがいくつあるかわからないので、処理しようがなくなってしまう。
		}
		else if(chunkNum == nextChunkNum) {
			sampleCount = nextSampleCount;
			dataRef = nextDataRef;
		}
		// 最終の部分だけ、考慮しないとだめ、次のデータはないのに、しばらくデータを読み込みつづける必要がでてくる。
		if(buffer.remaining() == 0) {
			// データがなくなったらあとはずっとchunkNumを応答しておく。
			return chunkNum;
		}
		// 次のデータを読み取る
		nextChunkNum	= buffer.getInt();
		nextSampleCount	= buffer.getInt();
		nextDataRef		= buffer.getInt();
		// すでにchunkNumとnextChunkNumが一致する場合(例えばはじめのアクセスでは発生する)
		if(chunkNum == nextChunkNum) {
			sampleCount	= nextSampleCount;
			dataRef		= nextDataRef;
		}
		return nextChunkNum;
	}
	public int getChunkNum() {
		return chunkNum;
	}
	public int getSampleCount() {
		return sampleCount;
	}
	public int getDataRef() {
		return dataRef;
	}
	public int getCount() {
		return count;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
