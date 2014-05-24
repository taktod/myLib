/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * トラック情報を保持
 * ここをみれば映像trakであるか音声trakであるかある程度把握できます。
 * @author taktod
 */
public class Tkhd extends Atom {
	private long creationTime;
	private long modificationTime;
	private int trackId;
	private int reserved1;
	private long duration;
	private int[] reserved2 = new int[2];
	private short layer;
	private short alternateGroup;
	private short volume = -1;
	private short reserved3;
	private int[] transformMatrix = new int[9];
	private int width = -1;
	private int height = -1;
	public Tkhd(int position, int size) {
		super(Tkhd.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		// tkhdの中身を解析していく。
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 4);
		analyzeFirstInt(buffer.getInt());
		int i;
		if(getVersion() == 0) {
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
		if(getVersion() == 0) {
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
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(super.toString("    "));
		if(width != -1 || height != 1 || volume != -1) {
			data.append("[width:").append(width)
			.append(" height:").append(height)
			.append(" volume:").append(volume)
			.append("]");
		}
		return data.toString();
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
