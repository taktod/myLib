package com.ttProject.container.mpegts;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * programPacket
 * @author taktod
 */
public abstract class ProgramPacket extends MpegtsPacket {
	private Bit8 pointerField = new Bit8(); // 0000 0000
	private Bit8 tableId = new Bit8(); // packet固定値
	private Bit1 sectionSyntaxIndicator = new Bit1(); // 1
	private Bit1 reservedFutureUse1 = new Bit1(); // 0
	private Bit2 reserved1 = new Bit2(); // 11
	private Bit12 sectionLength = new Bit12(); // 12bit
	private Bit16 programNumber = new Bit16(); // 16bit
	private Bit2 reserved2 = new Bit2(); // 11
	private Bit5 versionNumber = new Bit5(); // 00000
	private Bit1 currentNextOrder = new Bit1(); // 1
	private Bit8 sectionNumber = new Bit8(); // 00000000
	private Bit8 lastSectionNumber = new Bit8(); // 00000000
	public ProgramPacket(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(pointerField, tableId, sectionSyntaxIndicator,
				reservedFutureUse1, reserved1, sectionLength, programNumber,
				reserved2, versionNumber, currentNextOrder,
				sectionNumber, lastSectionNumber);
		super.setSize(8 + sectionLength.get());
	}
	/**
	 * sectionLengthの変更
	 * @param length
	 */
	protected void setSectionLength(int length) {
		// sectionLengthは後天的に変わることがあるので、設定できるようにしなければならない
		sectionLength.set(length);
		super.setSize(8 + sectionLength.get());
	}
	protected int getSectionLength() {
		return sectionLength.get();
	}
}
