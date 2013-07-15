package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.ProgramPacket;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Pmt
 * 475000100002B0120001C10000 E100F0000FE100F000B69BC0D9
 * @author taktod
 */
public class Pmt extends ProgramPacket {
	private Bit3 reserved1;
	private short pcrPid; // 13bit
	private Bit4 reserved2;
	private short programInfoLength; // 12bit (どうみてもこれ0なんだが・・・どうなるんだろう)

	private List<PmtElementaryField> fields = new ArrayList<PmtElementaryField>();
	// 以下programDescriptor
	// type 3bit pid 4bit esInfoLength eDescriptor
	public Pmt(ByteBuffer buffer) {
		this(0, buffer);
	}
	public Pmt(int position, ByteBuffer buffer) {
		super(position, buffer);
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		IReadChannel channel = new ByteReadChannel(getBuffer());
		analyzeHeader(channel);
		int size = getSectionLength() - 5; // 残りの読み込むべきデータ量
		// 自分のデータを読み込む
		reserved1 = new Bit3();
		Bit5 pcrPid_1 = new Bit5();
		Bit8 pcrPid_2 = new Bit8();
		reserved2 = new Bit4();
		Bit4 programInfoLength_1 = new Bit4();
		Bit8 programInfoLength_2 = new Bit8();
		Bit.bitLoader(channel, reserved1, pcrPid_1, pcrPid_2, reserved2, programInfoLength_1, programInfoLength_2);
		pcrPid = (short)((pcrPid_1.get() << 8) | pcrPid_2.get());
		programInfoLength = (short)((programInfoLength_1.get() << 8) | programInfoLength_2.get());
		// sectionLengthから残りのデータ量を見積もる。
		size -= 4;
		while(size > 4) {
			// 残りの部分がpmtElementaryFieldになる。
			PmtElementaryField elementaryField = new PmtElementaryField();
			elementaryField.analyze(channel);
			size -= elementaryField.getSize();
			fields.add(elementaryField);
		}
	}
	public int getPcrPid() {
		return pcrPid;
	}
	public List<PmtElementaryField> getFields() {
		return fields;
	}
	@Override
	public String toString() {
		return "Pmt: ";
	}
}
