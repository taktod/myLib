package com.ttProject.media.mpegts.descriptor;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 各descriptorのベースになる部分
 * @author taktod
 */
public abstract class Descriptor {
	/** 設定タグ(descriptorによってまちまち) */
	private Bit8 descriptorTag;
	/** 保持データ量 */
	private Bit8 descriptorLength;
	/**
	 * コンストラクタ
	 * @param tag
	 * @param length
	 */
	public Descriptor(Bit8 tag, Bit8 length) {
		descriptorTag = tag;
		descriptorLength = length;
	}
	/**
	 * タグ参照
	 * @return
	 */
	public Bit8 getDescriptorTag() {
		return descriptorTag;
	}
	/**
	 * 設定長さ参照
	 * @return
	 */
	public Bit8 getDescriptorLength() {
		return descriptorLength;
	}
	/**
	 * 実データサイズ参照
	 * @return
	 */
	public int getSize() {
		return descriptorLength.get() + 2; // タグの設定長さ + tag&lengthBit
	}
	/**
	 * descriptorを取り出す動作
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public static Descriptor getDescriptor(IReadChannel channel) throws Exception {
		// 先頭のデータを読み込んでTagがなんであるかみておく。
		Bit8 descriptorTag = new Bit8();
		Bit8 descriptorLength = new Bit8();
		Bit.bitLoader(channel, descriptorTag, descriptorLength);
		switch(DescriptorType.getType(descriptorTag.get())) {
		case service_descriptor:
			ServiceDescriptor serviceDescriptor = new ServiceDescriptor(descriptorLength);
			serviceDescriptor.analyze(channel);
			return serviceDescriptor;
		default: // 知らないデータは放置しておく
			throw new Exception("未定義の型がきました。");
		}
	}
}
