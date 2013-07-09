package com.ttProject.media.mpegts;

public enum CodecType {
	H264(0x1B);
	private final int value;
	private CodecType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static CodecType getType(int value) throws Exception {
		for(CodecType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new Exception("知らないコーデックタイプを検知しました。:" + Integer.toHexString(value));
	}
}
