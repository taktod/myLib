package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.library.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public class Tkhd extends Atom {
	private byte version;
	private int flags;
	private long creationTime;
	private long modificationTime;
	private int trackId;
	private int reserved1;
	private long duration;
	private int[] reserved2 = new int[2];
	private short layer;
	private short alternateGroup;
	private short volume;
	private short reserved3;
	private int[] transformMatrix = new int[9];
	private int width;
	private int height;
	public Tkhd(int size, int position) {
		super(Tkhd.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		// tkhdの中身を解析していく。
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 4);
		int head = buffer.getInt();
		int i;
		version = (byte)((head >> 24) & 0xFF);
		flags = (head & 0x00FFFFFF);
		if(version == 0) {
			buffer = BufferUtil.safeRead(ch, 80);
			creationTime = buffer.getInt();
			modificationTime = buffer.getInt();
		}
		else {
			buffer = BufferUtil.safeRead(ch, 92);
			creationTime = buffer.getLong();
			modificationTime = buffer.getLong();
		}
		trackId = buffer.getInt();
		reserved1 = buffer.getInt();
		if(version == 0) {
			duration = buffer.getInt();
		}
		else {
			duration = buffer.getLong();
		}
		for(i = 0;i < 2;i ++) {
			reserved2[i] = buffer.getInt();
		}
		layer = buffer.getShort();
		alternateGroup = buffer.getShort();
		volume = buffer.getShort();
		reserved3 = buffer.getShort();
		for(i = 0;i < 9;i ++) {
			transformMatrix[i] = buffer.getInt();
		}
		width = ((buffer.getInt() >> 16) & 0xFFFF);
		height = ((buffer.getInt() >> 16) & 0xFFFF);
		analyzed();
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(super.toString("    "));
		if(isAnalyzed()) {
			data.append("[width:").append(width)
			.append(" height:").append(height)
			.append(" volume:").append(volume)
			.append("]");
		}
		return data.toString();
	}
	public byte getVersion() {
		return version;
	}
	public int getFlags() {
		return flags;
	}
	public long getCreationTime() {
		return creationTime;
	}
	public long getModificationTime() {
		return modificationTime;
	}
	public int getTrackId() {
		return trackId;
	}
	public int getReserved1() {
		return reserved1;
	}
	public long getDuration() {
		return duration;
	}
	public int[] getReserved2() {
		return reserved2;
	}
	public short getLayer() {
		return layer;
	}
	public short getAlternateGroup() {
		return alternateGroup;
	}
	public short getVolume() {
		return volume;
	}
	public short getReserved3() {
		return reserved3;
	}
	public int[] getTransformMatrix() {
		return transformMatrix;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
}
