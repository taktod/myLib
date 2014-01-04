package com.ttProject.container.mp4.stsd.record;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.esds.EsTag;
import com.ttProject.container.mp4.esds.Tag;
import com.ttProject.container.mp4.esds.TagType;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

public class Esds extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Esds.class);
	// この部分はversion + flagsであるかどうかはわからない。
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Tag   esTag = null;
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
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		Bit8 tag = new Bit8();
		BitLoader loader = new BitLoader(channel);
		loader.load(version, flags);
		// ここは読み込みデータが1つになるっぽいか？
		loader = new BitLoader(channel);
		loader.load(tag);
		// ここで内部データをすべて読み込んでおきたい。
		switch(TagType.getType(tag)) {
		case EsTag:
			esTag = new EsTag(tag);
			break;
		default:
			logger.error(tag.get());
			throw new Exception("知らない型のtagを取得しました。");
		}
		esTag.minimumLoad(channel);
		logger.info(esTag);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
