package com.ttProject.media.aac;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ADTS方式であるとして、Aacのデータを解析します。
 * @see http://blog-imgs-18-origin.fc2.com/n/a/n/nanncyatte/aacfileheader.png
 * @author taktod
 */
public class AacManager extends Manager<Frame> {
	@Override
	public List<Frame> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
	@Override
	public Frame getUnit(IReadChannel source) throws Exception {
		// frameUnitを解析します。
		// headerは7バイトで構成されているので、7バイト存在しない場合は処理できません。
		if(source.size() - source.position() < 7) {
			return null;
		}
		int position = source.position();
		Bit4 syncBit1 = new Bit4();
		Bit8 syncBit2 = new Bit8();
		Bit1 id = new Bit1();
		Bit2 layer = new Bit2();
		Bit1 protectionAbsent = new Bit1();
		Bit2 profile = new Bit2();
		Bit4 samplingFrequenceIndex = new Bit4();
		Bit1 privateBit = new Bit1();
		Bit3 channelConfiguration = new Bit3();
		Bit1 originalFlg = new Bit1();
		Bit1 home = new Bit1();
		Bit1 copyrightIdentificationBit = new Bit1();
		Bit1 copyrightIdentificationStart = new Bit1();
		Bit5 frameSize1 = new Bit5();
		Bit8 frameSize2 = new Bit8();
		Bit3 adtsBufferFullness1 = new Bit3();
		Bit8 adtsBufferFullness2 = new Bit8();
		Bit2 noRawDataBlocksInFrame = new Bit2();
		Bit.bitLoader(source,
			syncBit1, syncBit2, id, layer, protectionAbsent, profile, samplingFrequenceIndex,
			privateBit, channelConfiguration, originalFlg, home,
			copyrightIdentificationBit, copyrightIdentificationStart, frameSize1, frameSize2,
			adtsBufferFullness1, adtsBufferFullness2, noRawDataBlocksInFrame);
		int size = (frameSize1.get() << 8) + frameSize2.get();
		return new Aac(position, size, id, layer, protectionAbsent, profile, samplingFrequenceIndex, privateBit, channelConfiguration, originalFlg, home, copyrightIdentificationBit, copyrightIdentificationStart, size, (adtsBufferFullness1.get() << 8) + adtsBufferFullness2.get(), noRawDataBlocksInFrame);
	}
/*	@Override
	public Frame getUnit(IReadChannel source) throws Exception {
		// frameUnitを解析します。
		// とりあえず7バイト読み込んで確認する必要がある。
		if(source.size() - source.position() < 7) {
			return null;
		}
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 7);
		int data = buffer.getShort();
		// 先頭12bitがsyncBit
		if((data & 0xFFF0) != 0xFFF0) {
			throw new Exception("syncbitが一致しません。");
		}
// 		Bit1 id = new Bit1((data & 0x08) >>> 3);
		// 0:mpeg4 1:mpeg2
		byte id = (byte)((data & 0x08) >>> 3);
//		Bit2 layer = new Bit2((data & 0x06) >>> 1);
		// layer常に0
		if((data & 0x6) != 0x00) {
			System.out.println(Integer.toHexString(data));
			throw new Exception("layerのbit値が一致しません。");
		}
//		Bit1 protectionAbsent = new Bit1(data & 0x01);
		byte protectionAbsent = (byte)(data & 0x01);
		data = buffer.get();
		// profile 00:Main 01:LC 10:SSR 11:reserved
//		Bit2 profile = new Bit2((data & 0xC0) >>> 6);
		byte profile = (byte)((data & 0xC0) >>> 6);
		if(profile == 3) {
			throw new Exception("reservedのprofileを検知しました。");
		}
		// index
//		Bit4 samplingFrequenceIndex = new Bit4((data & 0x3C) >>> 2);
		byte samplingFrequenceIndex = (byte)((data & 0x3C) >>> 2);
		// privateBitデフォルト0?
//		Bit1 privateBit = new Bit1((data & 0x02) >>> 2);
		byte privateBit = (byte)((data & 0x02) >>> 1);
		if(privateBit != 0) {
			System.out.println("privateBitが1でした。");
		}
		data = ((data & 0x01) << 8) + (buffer.get() & 0xFF);
		// channelConfiguration
//		Bit3 channelConfiguration = new Bit3((data & 0x01C0) >>> 6);
		int channelCount = (data & 0x01C0) >>> 6;
		// original or copy? 0:original 1:copy
//		Bit1 originalFlg = new Bit1((data & 0x20) >>> 5);
		byte originalFlg = (byte)((data & 0x20) >>> 5);
		// home ?
//		Bit1 home = new Bit1((data & 0x10) >>> 4);
		byte home = (byte)((data & 0x10) >>> 4);
//		Bit1 copyrightIdentificationBit = new Bit1((data & 0x08) >>> 3);
		byte copyrightIdentificationBit = (byte)((data & 0x08) >>> 3);
//		Bit1 copyrightIdentificationStart = new Bit1((data & 0x04) >>> 2);
		byte copyrightIdentificationStart = (byte)((data & 0x04) >>> 2);
		data = ((data & 0x03) << 16) + (buffer.getShort() & 0xFFFF);
		// 13bit 
		int frameSize = ((data & 0x03FFE0) >>> 5);
		data = ((data & 0x1F) << 8) + (buffer.get() & 0xFF);
		// ADTSバッファ残量 0x7FFの場合はVBRです。
		// 11bit
		int adtsBufferFullness = (data >>> 2);
//		Bit2 noRawDataBlocksInFrame = new Bit2(data & 0x03);
		byte noRawDataBlocksInFrame = (byte)(data & 0x03);
		return new Aac(position, frameSize, id, protectionAbsent, profile, samplingFrequenceIndex, privateBit, channelCount, originalFlg, home, copyrightIdentificationBit, copyrightIdentificationStart, adtsBufferFullness, noRawDataBlocksInFrame);
	}*/
}
