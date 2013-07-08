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
		Record record = Record.getRecord(new String(name).toLowerCase(), size, position);
		record.analyze(ch);
		ch.position(size + position);
		return record;
	}
}
