/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4.atom.stsd;

import java.nio.ByteBuffer;

import com.ttProject.util.BufferUtil;
import com.ttProject.nio.channels.IReadChannel;

public class RecordAnalyzer implements IRecordAnalyzer {
	public Record analyze(IReadChannel ch) throws Exception {
		int position = ch.position();
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		int size = buffer.getInt();
		byte[] name = new byte[4];
		buffer.get(name);
		// ここで知らないコーデックだった場合に例外がでるよ？
		Record record = Record.getRecord(new String(name).toLowerCase(), position, size);
		record.analyze(ch);
		ch.position(size + position);
		return record;
	}
}
