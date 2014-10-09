package com.ttProject.container.riff;

import com.ttProject.frame.CodecType;

/**
 * RiffCodecType def, for strh.
 * @author taktod
 */
public enum StrhRiffCodecType {
	MJPG(0x4D4A5047, CodecType.MJPEG),
	PCM(0x00000000, CodecType.NONE), // PCMっぽいけど、たぶんWaveFormatExがあるはず
	ADPCM(0x01000000, CodecType.NONE); // 別のところでcodec定義あるかも・・・
	private int code;
	private CodecType type;
	/**
	 * constructor
	 * @param codecType
	 */
	private StrhRiffCodecType(int code, CodecType codecType) {
		this.code = code;
		this.type = codecType;
	}
	public int intValue() {
		return code;
	}
	public CodecType getCodecType() {
		return type;
	}
	public static StrhRiffCodecType getValue(int value) {
		for(StrhRiffCodecType type : values()) {
			if(type.intValue() == value) {
				return type;
			}
		}
		throw new RuntimeException("unexpected value.:" + value);
	}
}
