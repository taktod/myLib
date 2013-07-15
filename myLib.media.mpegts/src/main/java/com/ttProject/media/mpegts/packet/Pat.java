package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.ProgramPacket;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Pat(Program Association Table)
 * 474000100000B00D0001C100000001F0002AB104B2
 * @author taktod
 */
public class Pat extends ProgramPacket {
	private short programNum; // 16bit
	private Bit3 reserved;
	private short programPid; // 13bit
	public Pat(ByteBuffer buffer) {
		this(0, buffer);
	}
	public Pat(int position, ByteBuffer buffer) {
		super(position, buffer);
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		IReadChannel channel = new ByteReadChannel(getBuffer());
		// 先頭の部分を解析しておく。
		analyzeHeader(channel);
		Bit8 programNum_1 = new Bit8();
		Bit8 programNum_2 = new Bit8();
		reserved = new Bit3();
		Bit5 programPid_1 = new Bit5();
		Bit8 programPid_2 = new Bit8();
		
		Bit.bitLoader(channel, programNum_1, programNum_2,
				reserved, programPid_1, programPid_2);
		programNum = (short)((programNum_1.get() << 8) | programNum_2.get());
		programPid = (short)((programPid_1.get() << 8) | programPid_2.get());
	}
	public short getProgramPId() {
		return programPid;
	}
	public String dump3() {
		StringBuilder data = new StringBuilder("Pat:");
		data.append(" pn:").append(Integer.toHexString(programNum));
		data.append(" r:").append(reserved);
		data.append(" pp:").append(Integer.toHexString(programPid));
		return data.toString();
	}
	@Override
	public String toString() {
		return "Pat: ";
	}
}
