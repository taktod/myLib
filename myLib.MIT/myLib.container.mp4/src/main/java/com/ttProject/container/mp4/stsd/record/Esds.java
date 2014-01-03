package com.ttProject.container.mp4.stsd.record;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

public class Esds extends Mp4Atom {
	// この部分はversion + flagsであるかどうかはわからない。
	private Bit8 version;
	private Bit24 flags;
	// 内部データのTag: Es_tag 0x03 DecoderConfig 0x04 DecoderSpecific 0x05 SlConfig 0x06
	// 内部データはtag + 1xxx xxxx 1xxx xxxx・・・という形の長さ
	// esTag: edId(16bit) flags(8bit)(0x00以外の場合は、データの読み込みが必要。どういうデータかはわからない。)
	// あとのデータは他のtagデータがはいっているみたい。
	
	// decoderConfig:objectType(8bit) flags(8bit) bufferSize(24bit) maxBitRate(32bit) avgBitRate(32bit)
	// あとのデータは他のtagデータがはいっているみたい。
	
	// decoderSpecific:aacの場合はdecoderSpecificInfoデータがはいっている。

	// SlConfig:slConfigデータ(指定サイズすべて)
	
	public Esds(Bit32 size, Bit32 name) {
		super(size, name);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
