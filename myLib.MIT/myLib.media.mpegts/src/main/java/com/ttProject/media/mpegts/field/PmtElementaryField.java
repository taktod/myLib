/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.descriptor.Descriptor;
import com.ttProject.nio.channels.IReadChannel;

/**
 * pmtのelementaryStreamのデータ記述部
 * @author taktod
 */
public class PmtElementaryField {
	// あたらしくトラックをつくった場合の次のpid
	private static short nextTrackPid = 0x0100;
	private Bit8 streamType;
	private Bit3 reserved1;
	private short pid; // 13bit
	private Bit4 reserved2;
	private short esInfoLength; // 12bit
	// ESDescriptor
	/*
	 * vlcで作成したmpegtsのデータにこのdescriptorの定義がありましたが、情報がみつからない・・・
	 * とりあえず・・・
	 * type:0x05 4 AC-3と書いてあった
	 * type:0x0A 4 00 00 00 00で埋まってた。
	 * の２種類だけ見つかりました。なんだろうねぇこれ・・・
	 * @see http://www.etherguidesystems.com/help/sdos/mpeg/semantics/mpeg-2/descriptors/Default.aspx
	 * 情報めっけ
	 */
	private List<Descriptor> descriptors = new ArrayList<Descriptor>();
//	private Descriptor esDescriptor; // 形式がわからないので、とりあえず放置
	public int getSize() {
		return 5 + esInfoLength;
	}
	/**
	 * 対象pidを取得
	 * @return
	 */
	public short getPid() {
		return pid;
	}
	public static PmtElementaryField makeNewField(CodecType codec) {
		PmtElementaryField elementField = new PmtElementaryField();
		elementField.streamType = new Bit8(codec.intValue());
		elementField.reserved1 = new Bit3(0x07);
		elementField.pid = nextTrackPid ++;
		elementField.reserved2 = new Bit4(0x0F);
		elementField.esInfoLength = 0;
		return elementField;
	}
	/**
	 * 対象コーデックタイプを取得
	 * @return
	 * @throws Exception
	 */
	public CodecType getCodecType() throws Exception {
		return CodecType.getType(streamType.get());
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(streamType);
		list.add(reserved1);
		list.add(new Bit5(pid >>> 8));
		list.add(new Bit8(pid));
		list.add(reserved2);
		list.add(new Bit4(esInfoLength >>> 8));
		list.add(new Bit8(esInfoLength));
		for(Descriptor descriptor : descriptors) {
			list.addAll(descriptor.getBits());
		}
		return list;
	}
	/**
	 * 保持descriptorを応答する
	 */
	public List<Descriptor> getDescriptors() {
		return new ArrayList<Descriptor>(descriptors);
	}
	/**
	 * 解析しておく。
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IReadChannel ch) throws Exception {
		streamType = new Bit8();
		reserved1 = new Bit3();
		Bit5 pid_1 = new Bit5();
		Bit8 pid_2 = new Bit8();
		reserved2 = new Bit4();
		Bit4 esInfoLength_1 = new Bit4();
		Bit8 esInfoLength_2 = new Bit8();
		BitLoader bitLoader = new BitLoader(ch);
		bitLoader.load(streamType, reserved1, pid_1, pid_2, reserved2,
				esInfoLength_1, esInfoLength_2);
		pid = (short)((pid_1.get() << 8) | pid_2.get());
		if(pid > nextTrackPid) {
			nextTrackPid = (short)(pid + 1);
		}
		esInfoLength = (short)((esInfoLength_1.get() << 8) | esInfoLength_2.get());
		int size = esInfoLength;
		while(size > 0) {
			Descriptor descriptor = Descriptor.getDescriptor(ch);
			size -= descriptor.getDescriptorLength().get() + 2; // データ長 + データtype&length定義分
			descriptors.add(descriptor);
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("  ");
		data.append("pmtElementrayField:");
		try {
			data.append(" type:").append(CodecType.getType(streamType.get()));
		}
		catch (Exception e) {
		}
		data.append(" r1:").append(reserved1);
		data.append(" pid:").append(Integer.toHexString(pid));
		data.append(" r2:").append(reserved2);
		data.append(" eil").append(Integer.toHexString(esInfoLength));
		for(Descriptor descriptor : descriptors) {
			data.append("\n");
			data.append(descriptor);
		}
		return data.toString();
	}
}
