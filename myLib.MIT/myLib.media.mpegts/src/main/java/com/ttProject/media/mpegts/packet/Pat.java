/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.mpegts.ProgramPacket;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Pat(Program Association Table)
 * 474000100000B00D0001C100000001F0002AB104B2
 * @author taktod
 */
public class Pat extends ProgramPacket {
	/** 巡回データカウンター */
	private static byte counter = 0;
	private short programNum; // 16bit // 1
	private Bit3 reserved; // 111
	private short programPid; // 13bit // 4096(0x1000)
	public Pat() throws Exception {
		super(0);
		setupDefault();
	}
	public Pat(ByteBuffer buffer) throws Exception {
		this(0, buffer);
	}
	public Pat(int position, ByteBuffer buffer) throws Exception {
		super(position);
		// bufferがある場合はそのまま読み込むものとします。
		analyze(new ByteReadChannel(buffer));
	}
	@Override
	public void setupDefault() throws Exception {
		analyzeHeader(new ByteReadChannel(new byte[]{
			0x47, 0x40, 0x00, 0x10, 0x00, 0x00, (byte)0xB0, 0x0D, 0x00, 0x01, (byte)0xC1, 0x00, 0x00
		}));
		programNum = 1;
		reserved = new Bit3(0x07);
		programPid = (short)0x1000;
		short length = 0;
		length += 5; // programPacket残り
		length += 4; // pat独自データ
		length += 4; // crc32
		setSectionLength(length);
	}
	@Override
	public List<Bit> getBits() {
		List<Bit> list = super.getBits();
		list.add(new Bit8(programNum >>> 8));
		list.add(new Bit8(programNum));
		list.add(reserved);
		list.add(new Bit5(programPid >>> 8));
		list.add(new Bit8(programPid));
		return list;
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		// 先頭の部分を解析しておく。
		analyzeHeader(ch);
		Bit8 programNum_1 = new Bit8();
		Bit8 programNum_2 = new Bit8();
		reserved = new Bit3();
		Bit5 programPid_1 = new Bit5();
		Bit8 programPid_2 = new Bit8();
		
		BitLoader bitLoader = new BitLoader(ch);
		bitLoader.load(programNum_1, programNum_2,
				reserved, programPid_1, programPid_2);
		programNum = (short)((programNum_1.get() << 8) | programNum_2.get());
		programPid = (short)((programPid_1.get() << 8) | programPid_2.get());
	}
	/**
	 * 対応しているpmtのpidを応答します。
	 * @return
	 */
	public short getProgramPid() {
		return programPid;
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		setContinuityCounter(counter ++);
		return super.getBuffer();
	}
	/**
	 * 巡回cc値を設定して動作するgetBuffer
	 * @param counter
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getBuffer(int counter) throws Exception {
		setContinuityCounter(counter);
		return super.getBuffer();
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("Pat:");
		data.append("\n").append(super.toString());
		data.append(" pn:").append(Integer.toHexString(programNum));
		data.append(" r:").append(reserved);
		data.append(" pp:").append(Integer.toHexString(programPid));
		return data.toString();
	}
}
