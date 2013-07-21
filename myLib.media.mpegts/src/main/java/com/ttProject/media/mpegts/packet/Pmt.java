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
	/** 巡回データカウンター */
	private static byte counter = 0;
	private static short pmtPid = 0x1000; // pmtのpid値
	private Bit3 reserved1;
	private short pcrPid; // 13bit
	private Bit4 reserved2;
	private short programInfoLength; // 12bit (どうみてもこれ0なんだが・・・どうなるんだろう)

	private List<PmtElementaryField> fields = new ArrayList<PmtElementaryField>();
	// 以下programDescriptor
	// type 3bit pid 4bit esInfoLength eDescriptor
	public Pmt() {
		super(0);
	}
	public Pmt(short pid) {
		super(0);
		pmtPid = pid;
	}
	public Pmt(ByteBuffer buffer) throws Exception {
		this(0, buffer);
	}
	public Pmt(int position, ByteBuffer buffer) throws Exception {
		super(position);
		analyze(new ByteReadChannel(buffer));
	}
	@Override
	public void setupDefault() throws Exception {
		byte b1 = (byte)(0x40 | (pmtPid >>> 8));
		byte b2 = (byte)(pmtPid & 0xFF);
		analyzeHeader(new ByteReadChannel(new byte[]{
				0x47, b1, b2, 0x10, 0x00, 0x00, 0x02, (byte)0xB0, 0x12, 0x01, (byte)0xC1, 0x00, 0x00
		}), counter ++);
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		return null;
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		analyzeHeader(ch, counter ++);
		pmtPid = getPid();
		if(counter > 0x0F) {
			counter = 0;
		}
		int size = getSectionLength() - 5; // 残りの読み込むべきデータ量
		// 自分のデータを読み込む
		reserved1 = new Bit3();
		Bit5 pcrPid_1 = new Bit5();
		Bit8 pcrPid_2 = new Bit8();
		reserved2 = new Bit4();
		Bit4 programInfoLength_1 = new Bit4();
		Bit8 programInfoLength_2 = new Bit8();
		Bit.bitLoader(ch, reserved1, pcrPid_1, pcrPid_2, reserved2, programInfoLength_1, programInfoLength_2);
		pcrPid = (short)((pcrPid_1.get() << 8) | pcrPid_2.get());
		programInfoLength = (short)((programInfoLength_1.get() << 8) | programInfoLength_2.get());
		// sectionLengthから残りのデータ量を見積もる。
		size -= 4;
		while(size > 4) {
			// 残りの部分がpmtElementaryFieldになる。
			PmtElementaryField elementaryField = new PmtElementaryField();
			elementaryField.analyze(ch);
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
		StringBuilder data = new StringBuilder();
		data.append("Pmt:");
		data.append("\n").append(super.toString());
		data.append(" r1:").append(reserved1);
		data.append(" pp:").append(Integer.toHexString(pcrPid));
		data.append(" r2:").append(reserved2);
		data.append(" pil:").append(Integer.toHexString(programInfoLength));
		// あとはfieldのデータ
		for(PmtElementaryField pefield : fields) {
			data.append("\n");
			data.append(pefield);
		}
		return data.toString();
	}
}
