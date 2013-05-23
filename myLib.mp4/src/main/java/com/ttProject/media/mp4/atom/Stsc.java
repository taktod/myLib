package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
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
		 * 1,3 4,2 6,1とある場合
		 * 1,3
		 * 2,3
		 * 3,3
		 * 4,2
		 * 5,2
		 * 6,1
		 * 7,1...となる
		 */
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
