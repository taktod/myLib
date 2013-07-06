package com.ttProject.media.mp4.atom.stsd;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.media.mp4.atom.stsd.data.Avcc;
import com.ttProject.nio.channels.IFileReadChannel;

@SuppressWarnings("unused")
public abstract class VideoRecord extends Record {
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
	private Avcc avcc = null;
//	private * boxes;
	public VideoRecord(String name, int position, int size) {
		super(name, position, size);
	}
	public Avcc getAvcc() {
		return avcc;
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
		// ぶっちゃげここのデータはどうでもいい、ほしいのは、mediaSequenceHeader
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
		// この部分の読み込みは別プログラムにした方がいいと思う。
		while(ch.position() < (getSize() + getPosition())) {
			int position = ch.position();
			buffer = BufferUtil.safeRead(ch, 8);
			byte[] name = new byte[4];
			int size = buffer.getInt();
			buffer.get(name);
			String tag = (new String(name)).toLowerCase();
			if("avcc".equals(tag)) {
				avcc = new Avcc(size, position);
				avcc.analyze(ch);
			}
//			System.out.println(tag);
			// tagがavccなら読み込んでおきたいところ。
			// avccだったら中身すべてがmediaSequenceHeaderなので保持しておく必要あり(flvにするため)
			// とりあえずavccだけ解析するふりをしておく。(解析もなにも中身すべてが対象のデータ)
//			System.out.println("position:" + Integer.toHexString(position) + " size:" + Integer.toHexString(size) + " tag:" + tag);
			ch.position(position + size);
		}
	}
}
