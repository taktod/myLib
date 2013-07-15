package com.ttProject.media.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

/**
 * プログラム系のpacketの親クラス
 * @author taktod
 */
public class ProgramPacket extends Packet {
	private Bit8 pointerField;
	private Bit8 tableId;
	private Bit1 sectionSyntaxIndicator;
	private Bit1 zero;
	private Bit2 reserved1;
	private short sectionLength; // 12bit
	private short programNumber; // 16bit
	private Bit2 reserved2;
	private Bit5 versionNumber;
	private Bit1 currentNextOrder;
	private Bit8 sectionNumber;
	private Bit8 lastSectionNumber;
	/**
	 * コンストラクタ
	 * @param position
	 * @param buffer
	 */
	public ProgramPacket(int position, ByteBuffer buffer) {
		super(position, buffer);
	}
	/**
	 * ヘッダー部分の解析補助補助
	 */
	protected void analyzeHeader(IReadChannel channel) throws Exception {
		System.out.println("analyzeHeader:programPacket");
		super.analyzeHeader(channel);
		pointerField = new Bit8();
		tableId = new Bit8();
		sectionSyntaxIndicator = new Bit1();
		zero = new Bit1();
		reserved1 = new Bit2();
		Bit4 sectionLength_1 = new Bit4();
		Bit8 sectionLength_2 = new Bit8();
		Bit8 programNumber_1 = new Bit8();
		Bit8 programNumber_2 = new Bit8();
		reserved2 = new Bit2();
		versionNumber = new Bit5();
		currentNextOrder = new Bit1();
		sectionNumber = new Bit8();
		lastSectionNumber = new Bit8();
		Bit.bitLoader(channel, pointerField, tableId, sectionSyntaxIndicator, zero, reserved1,
				sectionLength_1, sectionLength_2, programNumber_1, programNumber_2,
				reserved2, versionNumber, currentNextOrder, sectionNumber, lastSectionNumber);
		sectionLength = (short)((sectionLength_1.get() << 8) | sectionLength_2.get());
		programNumber = (short)((programNumber_1.get() << 8) | programNumber_2.get());
		System.out.println(dump1());
	}
	public short getSectionLength() {
		return sectionLength;
	}
	public String dump1() {
		StringBuilder data = new StringBuilder("pPacket:");
		data.append(" ti:").append(Integer.toHexString(tableId.get()));
		data.append(" sso:").append(sectionSyntaxIndicator);
		data.append(" z:").append(zero);
		data.append(" r1:").append(reserved1);
		data.append(" sl:").append(Integer.toHexString(sectionLength));
		data.append(" pn:").append(Integer.toHexString(programNumber));
		data.append(" r2:").append(reserved2);
		data.append(" vn:").append(versionNumber);
		data.append(" cno:").append(currentNextOrder);
		data.append(" sn:").append(sectionNumber);
		data.append(" lsn:").append(lastSectionNumber);
		return data.toString();
	}
}
