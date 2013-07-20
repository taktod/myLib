package com.ttProject.media.mpegts;

import java.util.List;

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
public abstract class ProgramPacket extends Packet {
	private Bit8 pointerField; // 0000 0000
	private Bit8 tableId; // packet固定値
	private Bit1 sectionSyntaxIndicator; // 1
	private Bit1 reservedFutureUse1; // 0
	private Bit2 reserved1; // 11
	private short sectionLength; // 12bit
	private short programNumber; // 16bit
	private Bit2 reserved2; // 11
	private Bit5 versionNumber; // 00000
	private Bit1 currentNextOrder; // 1
	private Bit8 sectionNumber; // 00000000
	private Bit8 lastSectionNumber; // 00000000
	/**
	 * コンストラクタ
	 * @param position
	 * @param buffer
	 */
	public ProgramPacket(int position) {
		super(position);
	}
	/**
	 * ヘッダー部分の解析補助補助
	 */
	protected void analyzeHeader(IReadChannel channel, byte counter) throws Exception {
		super.analyzeHeader(channel, counter);
		pointerField = new Bit8();
		tableId = new Bit8();
		sectionSyntaxIndicator = new Bit1();
		reservedFutureUse1 = new Bit1();
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
		Bit.bitLoader(channel, pointerField, tableId, sectionSyntaxIndicator, reservedFutureUse1, reserved1,
				sectionLength_1, sectionLength_2, programNumber_1, programNumber_2,
				reserved2, versionNumber, currentNextOrder, sectionNumber, lastSectionNumber);
		sectionLength = (short)((sectionLength_1.get() << 8) | sectionLength_2.get());
		programNumber = (short)((programNumber_1.get() << 8) | programNumber_2.get());
	}
	public short getSectionLength() {
		return sectionLength;
	}
	public void setSectionLength(short length) {
		sectionLength = length;
	}
	@Override
	public List<Bit> getBits() {
		List<Bit> list = super.getBits();
		list.add(pointerField);
		list.add(tableId);
		list.add(sectionSyntaxIndicator);
		list.add(reservedFutureUse1);
		list.add(reserved1);
		list.add(new Bit4(sectionLength >>> 8));
		list.add(new Bit8(sectionLength));
		list.add(new Bit8(programNumber >>> 8));
		list.add(new Bit8(programNumber));
		list.add(reserved2);
		list.add(versionNumber);
		list.add(currentNextOrder);
		list.add(sectionNumber);
		list.add(lastSectionNumber);
		return list;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(" ");
		data.append("parentPacket:");
		data.append(" ti:").append(Integer.toHexString(tableId.get()));
		data.append(" sso:").append(sectionSyntaxIndicator);
		data.append(" rfu1:").append(reservedFutureUse1);
		data.append(" r1:").append(reserved1);
		data.append(" sl:").append(Integer.toHexString(sectionLength));
		data.append(" pn:").append(Integer.toHexString(programNumber));
		data.append(" r2:").append(reserved2);
		data.append(" vn:").append(versionNumber);
		data.append(" cno:").append(currentNextOrder);
		data.append(" sn:").append(sectionNumber);
		data.append(" lsn:").append(lastSectionNumber);
		data.append("\n").append(super.toString());
		return data.toString();
	}
}
