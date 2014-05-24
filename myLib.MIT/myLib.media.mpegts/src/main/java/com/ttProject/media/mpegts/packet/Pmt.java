/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.mpegts.ProgramPacket;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Pmt
 * marioの
 * 47500010
 * 0002B0170001C10000
 * E100F000
 * 1BE100F000(h264)
 * 0FE101F000(aac)
 * 2F44B99B(CRC32)
 * 
 * rtypeDelta aacのみになったやつ
 * 47500010
 * 0002B0120001C10000
 * E100F000
 * 0FE100F000(aac)
 * B69BC0D9(CRC32)
 * 
 * 
 * vlcが出力したデータ
 * 474042309500FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
 * [      ]ここまでは普通
 *         []adaptationfieldで埋めてある。
 * 0002B01E0001E30000(programPacketで共通化している部分)
 *   []tableId 2
 *      [ ]sectionLength
 *         [  ]programNumber 1
 *             [] version numberが0x11になってる。
 *                   [  ] lastSectionNumberが0ではない
 * E044F000
 * [  ] pcrPidが0x44
 *     [  ]ふつう
 * 81E044F00C 050441432D330A0400000000(1トラック分しかないじゃんw)
 * 81ED7715(crc32)
 * 
 * 0002B01E0001E30000E044F00081E044F00C050441432D330A040000000081ED7715
 * 0002B01E0001E30000E044F00081E044F00C050441432D330A040000000081ED7715
 * 0002B01E0001E30000E044F00081E044F00C050441432D330A040000000081ED7715
 * 
 * 途中から長さがかわってた。始めは1Bのデータ(h264のデータが追加されてますね。)
 * 0002B0290001E50000E045F00081E044F00C050441432D330A04000000001BE045F0060A040000000097DACB3E
 * 0002B0290001E50000
 * E045F000
 * 81E044F00C   05 04 41432D33 0A 04 00000000
 * [1B]E045F006 0A 04 00000000
 * 97DACB3E
 * @author taktod
 */
public class Pmt extends ProgramPacket {
	/** 巡回データカウンター */
	private static byte counter = 0;
	private short pmtPid = 0x1000; // pmtのpid値
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
		}));
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
	public void analyze(IReadChannel ch) throws Exception {
		analyzeHeader(ch);
		pmtPid = getPid();
		int size = getSectionLength() - 5; // 残りの読み込むべきデータ量
		// 自分のデータを読み込む
		reserved1 = new Bit3();
		Bit5 pcrPid_1 = new Bit5();
		Bit8 pcrPid_2 = new Bit8();
		reserved2 = new Bit4();
		Bit4 programInfoLength_1 = new Bit4();
		Bit8 programInfoLength_2 = new Bit8();
		BitLoader bitLoader = new BitLoader(ch);
		bitLoader.load(reserved1, pcrPid_1, pcrPid_2, reserved2, programInfoLength_1, programInfoLength_2);
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
	public short getPcrPid() {
		return pcrPid;
	}
	public void setPcrPid(short pcrPid) {
		this.pcrPid = pcrPid;
	}
	public List<PmtElementaryField> getFields() {
		return new ArrayList<PmtElementaryField>(fields);
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		setContinuityCounter(counter ++);
		return super.getBuffer();
	}
	/**
	 * 巡回cc値を設定して動作するgetBuffer
	 * @param counter
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getBuffer(int counter) throws Exception {
		setContinuityCounter(counter);
		return super.getBuffer();
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
