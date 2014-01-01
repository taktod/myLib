package com.ttProject.container.mpegts.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * 各descriptorのベースになる部分
 * @author taktod
 */
public abstract class Descriptor {
	/** 設定タグ(descriptorによってまちまち) */
	private final Bit8 descriptorTag; // これは固定
	/** 保持データ量 */
	private Bit8 descriptorLength = new Bit8(); // これは可変っていうか、設定データによって変わる。
	/**
	 * コンストラクタ
	 * @param tag
	 * @param length
	 */
	public Descriptor(Bit8 tag, Bit8 length) {
		this(tag);
		descriptorLength = length;
	}
	public Descriptor(Bit8 tag) {
		descriptorTag = tag;
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
	 * 設定長さ設定
	 * @param length
	 */
	public void setDescriptorLength(Bit8 length) {
		descriptorLength = length;
	}
	/**
	 * 実データサイズ参照
	 * @return
	 */
	public int getSize() {
		return descriptorLength.get() + 2; // タグの設定長さ + tag&lengthBit
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(descriptorTag);
		list.add(descriptorLength);
		return list;
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
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(descriptorTag, descriptorLength);
		switch(DescriptorType.getType(descriptorTag.get())) {
		case registration_descriptor:
			RegistrationDescriptor registrationDescriptor = new RegistrationDescriptor(descriptorLength);
			registrationDescriptor.load(channel);
			return registrationDescriptor;
		case ISO_639_language_descriptor:
			ISO639LanguageDescriptor iso639LanguageDescriptor = new ISO639LanguageDescriptor(descriptorLength);
			iso639LanguageDescriptor.load(channel);
			return iso639LanguageDescriptor;
		case service_descriptor:
			ServiceDescriptor serviceDescriptor = new ServiceDescriptor(descriptorLength);
			serviceDescriptor.load(channel);
			return serviceDescriptor;
		default: // 知らないデータは放置しておく
			throw new Exception("未定義の型がきました。");
		}
	}
	public abstract void load(IReadChannel channel) throws Exception;
}