package com.ttProject.media.mp4.atom.stsd;

import java.nio.ByteBuffer;

import com.ttProject.library.BufferUtil;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public abstract class AudioRecord extends Record {
	private byte[] unknown1 = new byte[6];
	private short dataReferenceIndex;
	private int unknown2;
	private int unknown3;
	private short channelCount;
	private short sampleSize;
	private short unknown4;
	private short unknown5;
	private int sampleRate;
//	private * boxes;
	public AudioRecord(String name, int size, int position) {
		super(name, size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
		ByteBuffer buffer = BufferUtil.safeRead(ch, 28);
		buffer.get(unknown1);
		dataReferenceIndex = buffer.getShort();
		unknown2 = buffer.getInt(); // ここのデータhi wordがinnerVersion lo wordがrevisionLevelという情報もあり。
		// innerVersionが0でない場合は、sampleRate以降にも情報が16バイトある可能性あり。(未確認)
		unknown3 = buffer.getInt();
		channelCount = buffer.getShort();
		sampleSize = buffer.getShort();
		unknown4 = buffer.getShort();
		unknown5 = buffer.getShort();
		sampleRate = buffer.getInt();
		// 以下サブタグ(size:4 tag:4 body:x)
		// esdsの中身を解析するとaacのmediaSequenceHeaderが見えてくる。
		// この部分の読み込みは別プログラムにした方がいいと思う。
		while(ch.position() < (getSize() + getPosition())) {
			int position = ch.position();
			buffer = BufferUtil.safeRead(ch, 8);
			byte[] name = new byte[4];
			int size = buffer.getInt();
			buffer.get(name);
			String tag = (new String(name)).toLowerCase();
			// tagがesdsなら読み込んでおきたいところ。
//			System.out.println("position:" + Integer.toHexString(position) + " size:" + Integer.toHexString(size) + " tag:" + tag);
			ch.position(position + size);
		}
	}
}
