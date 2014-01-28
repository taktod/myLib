package com.ttProject.container.mpegts.descriptor;

import com.ttProject.container.mpegts.field.IDescriptorHolder;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit8;

public class ISO639LanguageDescriptor extends Descriptor {
	private Bit24 ISO639LanguageCode;
	private Bit8 audioType;
	// legendってあるけど・・・
	/**
	 * コンストラクタ(解析用)
	 * @param descriptorLength
	 */
	public ISO639LanguageDescriptor(Bit8 descriptorLength, IDescriptorHolder holder) {
		super(new Bit8(DescriptorType.ISO_639_language_descriptor.intValue()), descriptorLength, holder);
	}
	/**
	 * コンストラクタ(書き込み用)
	 */
	public ISO639LanguageDescriptor(IDescriptorHolder holder) {
		super(new Bit8(DescriptorType.ISO_639_language_descriptor.intValue()), holder);
	}
	public Bit24 getISO639LanguageCode() {
		return ISO639LanguageCode;
	}
	public Bit8 getAudioType() {
		return audioType;
	}
	/**
	 * 解析動作
	 * @param channel
	 * @throws Exception
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int length = getDescriptorLength().get();
		if(length == 4) {
			BitLoader loader = new BitLoader(channel);
			ISO639LanguageCode = new Bit24();
			audioType = new Bit8();
			loader.load(ISO639LanguageCode, audioType);
		}
		else {
			throw new Exception("意図しないデータサイズを受け取りました");
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("    ");
		data.append("ISO639LanguageDescriptor:");
		data.append(" length:").append(Integer.toHexString(getDescriptorLength().get()));
		data.append(" ISO639LanguageCode:").append(Integer.toHexString(ISO639LanguageCode.get()));
		data.append(" audioType:").append(audioType);
		return data.toString();
	}
}
