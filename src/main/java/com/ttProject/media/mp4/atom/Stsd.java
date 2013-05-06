package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.library.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.media.mp4.atom.stsd.IRecordAnalyzer;
import com.ttProject.media.mp4.atom.stsd.Record;
import com.ttProject.nio.channels.IFileReadChannel;

public class Stsd extends Atom {
	private byte version;
	private int flags;
	private int trackCount;
	private List<Record> records = new ArrayList<Record>();
	public Stsd(int size, int position) {
		super(Stsd.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		int head = buffer.getInt();
		version = (byte)((head >> 24) & 0xFF);
		flags = (head & 0x00FFFFFF);
		trackCount = buffer.getInt();
		// trackCountは1であることを望んでおきます。(マルチトラックはやらない。)
		if(trackCount != 1) {
			throw new Exception("stsdのtrack数が1ではありませんでした。作者の知らない形なので必要であれば解析を依頼してください。");
		}
		if(analyzer instanceof IRecordAnalyzer) {
			IRecordAnalyzer recordAnalyzer = (IRecordAnalyzer)analyzer;
			for(int i = 0;i < trackCount;i ++) {
				records.add(recordAnalyzer.analyze(ch));
			}
		}
	}
	public byte getVersion() {
		return version;
	}
	public int getFlags() {
		return flags;
	}
	public List<Record> getRecords() {
		return records;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
