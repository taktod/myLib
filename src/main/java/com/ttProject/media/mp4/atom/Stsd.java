package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.library.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.media.mp4.atom.item.StsdRecord;
import com.ttProject.nio.channels.IFileReadChannel;

public class Stsd extends Atom {
	private byte version;
	private int flags;
	private int trackCount;
	private List<StsdRecord> records = new ArrayList<StsdRecord>();
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
/*		System.out.println("version:" + version);
		System.out.println("flags:" + flags);
		System.out.println("trackCount:" + trackCount);*/
		// trackCountは1であることを望んでおきます。(マルチトラックはやらない。)
		if(trackCount != 1) {
			throw new Exception("stsdのtrack数が1ではありませんでした。作者の知らない形なので必要であれば解析を依頼してください。");
		}
		for(int i = 0;i < trackCount;i ++) {
			int position = ch.position();
			buffer = BufferUtil.safeRead(ch, 8);
			int size = buffer.getInt();
			byte[] name = new byte[4];
			buffer.get(name);
			StsdRecord record = StsdRecord.getRecord((new String(name)).toLowerCase(), size, position);
			record.analyze(ch);
			records.add(record);
			ch.position(size + position);
		}
	}
	public byte getVersion() {
		return version;
	}
	public int getFlags() {
		return flags;
	}
	public List<StsdRecord> getRecords() {
		return records;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
