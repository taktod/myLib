package com.ttProject.media.mp4.atom.item;

import java.nio.ByteBuffer;

import com.ttProject.library.BufferUtil;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public abstract class VideoRecord extends StsdRecord {
	private byte[] unknown1 = new byte[6];
	private short dataReferenceIndex;
	private short unknown2;
	private short unknown3;
	private int unknown4;
	private int unknown5;
	private int unknown6;
	private short width;
	private short height;
	private int horizontalResolution;
	private int verticalResolution;
	private int unknown7;
	private short frameCount;
	private byte nameSize;
	private byte[] name = new byte[31];
	private short depth;
	private short unknown8;
//	private * boxes;
	public VideoRecord(String name, int size, int position) {
		super(name, size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
		// ぶっちゃげここのデータはどうでもいい、ほしいのは、
		ByteBuffer buffer = BufferUtil.safeRead(ch, 78);
		buffer.get(unknown1);
		dataReferenceIndex = buffer.getShort();
		unknown2 = buffer.getShort();
		unknown3 = buffer.getShort();
		unknown4 = buffer.getInt();
		unknown5 = buffer.getInt();
		unknown6 = buffer.getInt();
		width = buffer.getShort();
		height = buffer.getShort();
		horizontalResolution = buffer.getInt();
		verticalResolution = buffer.getInt();
		unknown7 = buffer.getInt();
		frameCount = buffer.getShort();
		nameSize = buffer.get();
		buffer.get(name);
		depth = buffer.getShort();
		unknown8 = buffer.getShort();
		// 以下サブタグ(size:4byte tag:4byte data:xbyte)
		// サブタグのavcCがmediaSequenceHeaderになります。(自身がh.264の場合)
	}
}
