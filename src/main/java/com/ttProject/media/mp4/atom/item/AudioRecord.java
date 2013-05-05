package com.ttProject.media.mp4.atom.item;

import java.nio.ByteBuffer;

import com.ttProject.library.BufferUtil;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public abstract class AudioRecord extends StsdRecord {
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
		unknown2 = buffer.getInt();
		unknown3 = buffer.getInt();
		channelCount = buffer.getShort();
		sampleSize = buffer.getShort();
		unknown4 = buffer.getShort();
		unknown5 = buffer.getShort();
		sampleRate = buffer.getInt();
		// 以下サブタグ(size:4 tag:4 body:x)
		// esdsの中身を解析するとaacのmediaSequenceHeaderが見えてくる。
	}
}
