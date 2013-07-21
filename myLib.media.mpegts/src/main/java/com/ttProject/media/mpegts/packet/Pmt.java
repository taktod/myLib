package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.Crc32;
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
	public Pmt() throws Exception {
		super(0);
		setupDefault();
	}
	public Pmt(short pid) throws Exception {
		super(0);
		pmtPid = pid;
		setupDefault();
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
				0x47, b1, b2, 0x10, 0x00, 0x02, (byte)0xB0, 0x0D, 0x00, 0x01, (byte)0xC1, 0x00, 0x00
		}), counter ++);
		reserved1 = new Bit3(0x07);
		pcrPid = 0x0100; // トラックは0x0100〜はじめるとして、一番目が固定でpcrになるようにしておく。
		reserved2 = new Bit4(0x0F);
		programInfoLength = 0;
		// あとはトラック情報なんだが、そこはコーデック情報依存なので、あとでなんとかしておく。
	}
	@Override
	public List<Bit> getBits() {
		List<Bit> list = super.getBits();
		list.add(reserved1);
		list.add(new Bit5(pcrPid >>> 8));
		list.add(new Bit8(pcrPid));
		list.add(reserved2);
		list.add(new Bit4(programInfoLength >>> 8));
		list.add(new Bit8(programInfoLength));
		for(PmtElementaryField pefield : fields) {
			list.addAll(pefield.getBits());
		}
		return list;
	}
	public void addNewField(PmtElementaryField field) {
		if(!fields.contains(field)) {
			fields.add(field);
			// 追加したらデータの計算しなおしを実行する必要あり。
			short length = 0;
			// programPacket由来
			length += 5;
			// pmtのデータ
			length += 4;
			// fieldの長さ
			for(PmtElementaryField elementaryField : fields) {
				length += (short)elementaryField.getSize();
			}
			// crc32
			length += 4;
			setSectionLength(length);
		}
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		// 情報をbit配列に戻して応答する。
		List<Bit> bitsList = getBits();
		ByteBuffer buffer = Bit.bitConnector(bitsList.toArray(new Bit[]{}));
		// あとはcrc32を計算するだけ。
		buffer.position(5);
		Crc32 crc32 = new Crc32();
		while(buffer.remaining() > 0) {
			crc32.update(buffer.get());
		}
		int crc32Val = (int)crc32.getValue();
		bitsList.add(new Bit8(crc32Val >>> 24));
		bitsList.add(new Bit8(crc32Val >>> 16));
		bitsList.add(new Bit8(crc32Val >>> 8));
		bitsList.add(new Bit8(crc32Val));
		return Bit.bitConnector(bitsList.toArray(new Bit[]{}));
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
		return new ArrayList<PmtElementaryField>(fields);
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
