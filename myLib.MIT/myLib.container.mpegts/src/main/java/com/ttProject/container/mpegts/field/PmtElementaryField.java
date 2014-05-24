/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.container.mpegts.CodecType;
import com.ttProject.container.mpegts.descriptor.Descriptor;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * pmtのelementaryStreamのデータ記述部
 * @author taktod
 */
public class PmtElementaryField implements IDescriptorHolder {
	// あたらしくトラックをつくった場合の次のpid(TODO この部分を使い回せないおかげで、ちょっとした例外がでてしまった。staticをはずした方がよさそう。)
	private Bit8  streamType   = new Bit8();
	private Bit3  reserved1    = new Bit3();
	private Bit13 pid          = new Bit13(); // 13bit
	private Bit4  reserved2    = new Bit4();
	private Bit12 esInfoLength = new Bit12(); // 12bit
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
	
	// このtrackが名乗ったらいいのでは？と思われる推奨streamId値
	private int suggestStreamId;
	public int getSuggestStreamId() {
		return suggestStreamId;
	}
	public void setSuggestStreamId(int suggestStreamId) {
		this.suggestStreamId = suggestStreamId;
	}
	/**
	 * デフォルトコンストラクタ
	 */
	public PmtElementaryField() {
	}
	public PmtElementaryField(Bit8 streamType, Bit3 reserved1,
			Bit13 pid, Bit4 reserved2, Bit12 esInfoLength) {
		this.streamType   = streamType;
		this.reserved1    = reserved1;
		this.pid          = pid;
		this.reserved2    = reserved2;
		this.esInfoLength = esInfoLength;
	}
	public int getSize() {
		return 5 + esInfoLength.get();
	}
	/**
	 * 対象pidを取得
	 * @return
	 */
	public short getPid() {
		return (short)pid.get();
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
		list.add(pid);
		list.add(reserved2);
		list.add(esInfoLength);
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
	public void load(IReadChannel ch) throws Exception {
		BitLoader bitLoader = new BitLoader(ch);
		bitLoader.load(streamType, reserved1, pid, reserved2, esInfoLength);
		int size = esInfoLength.get();
		while(size > 0) {
			Descriptor descriptor = Descriptor.getDescriptor(ch, this);
			size -= descriptor.getDescriptorLength().get() + 2; // データ長 + データtype&length定義分
			descriptors.add(descriptor);
		}
	}
	/**
	 * サイズを更新すべき場合の処理
	 */
	@Override
	public void updateSize() {
		
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
		data.append(" pid:").append(Integer.toHexString(pid.get()));
		data.append(" r2:").append(reserved2);
		data.append(" eil").append(Integer.toHexString(esInfoLength.get()));
		for(Descriptor descriptor : descriptors) {
			data.append("\n");
			data.append(descriptor);
		}
		return data.toString();
	}
}
