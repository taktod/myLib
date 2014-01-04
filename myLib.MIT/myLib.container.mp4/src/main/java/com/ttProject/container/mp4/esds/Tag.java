package com.ttProject.container.mp4.esds;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.Data;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * mp4のesdsに含まれるタグ情報
 * @author taktod
 */
public abstract class Tag extends Data {
	private final Bit8 tag;
	/**
	 * コンストラクタ
	 * @param tag
	 */
	public Tag(Bit8 tag) {
		this.tag = tag;
	}
	/**
	 * データを読み込む
	 * @param channel
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// sizeについて読み込む必要がある。
		Bit1 continueBit = new Bit1();
		Bit7 sizeBit     = new Bit7();
		BitLoader loader = new BitLoader(channel);
		int size = 0;
		do {
			loader.load(continueBit, sizeBit);
			size = size << 7 | sizeBit.get();
		} while(continueBit.get() == 1);
		setSize(size);
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(getClass().getSimpleName());
		data.append(" tag:").append(tag.get());
		data.append(" size:").append(getSize());
		return data.toString();
	}
}
