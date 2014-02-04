package com.ttProject.container.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * programPacket
 * @author taktod
 * 
 * TODO programPacketの場合は、crc32を確認して同じだったら読み込みを実施しないという動作が必要となるとおもいます。
 * 読み込みをスキップすると動作が軽くなる。
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
		// TODO ここの処理はminimumLoadからloadに変更したいとおもいます。
		// 理由はcrc32のみ先に取得するすべをつくっておきたいので
		// crc32を最速で作成する動作のみ、ここにいれたいと思います。
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(pointerField, tableId, sectionSyntaxIndicator,
				reservedFutureUse1, reserved1, sectionLength, programNumber,
				reserved2, versionNumber, currentNextOrder,
				sectionNumber, lastSectionNumber);
		if(isAdaptationFieldExist()) {
			super.setSize(8 + getAdaptationField().getLength() + 1 + sectionLength.get());
		}
		else {
			super.setSize(8 + sectionLength.get());
		}
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
	@Override
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return BufferUtil.connect(
			super.getHeaderBuffer(),
			connector.connect(
				pointerField, tableId, sectionSyntaxIndicator, reservedFutureUse1,
				reserved1, sectionLength, programNumber, reserved2, versionNumber,
				currentNextOrder, sectionNumber, lastSectionNumber
			)
		);
	}
	/**
	 * crcの値を参照します
	 * @return
	 */
	public abstract int getCrc();
	/**
	 * crcの値を算出します
	 * @param buffer
	 * @return
	 */
	protected int calculateCrc(ByteBuffer buffer) {
		// TODO この計算の仕方は正確にはただしくない。
		// 先頭にadaptationFieldがあって位置がずれている場合にきちんと動作しません。
		// 本当はpointerFieldを抜いたデータから計算すべき
		Crc32 crc32 = new Crc32();
		ByteBuffer tmpBuffer = buffer.duplicate();
		tmpBuffer.position(5);
		while(tmpBuffer.remaining() > 0) {
			crc32.update(tmpBuffer.get());
		}
		return (int)crc32.getValue();
	}
}
