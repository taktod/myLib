package com.ttProject.container.mkv;

public enum CodecType {
	V_MPEG4_ISO_AVC("V_MPEG4/ISO/AVC"),
	V_VP8("V_VP8"),
	A_AAC("A_AAC"),
	A_MPEG_L3("A_MPEG/L3"),
	A_VORBIS("A_VORBIS");
	private final String name;
	private CodecType(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	/**
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static CodecType getCodecType(String data) throws Exception {
		if(data.startsWith("V_MPEG4")) {
			if(data.contains("AVC")) {
				return V_MPEG4_ISO_AVC;
			}
		}
		else if(data.startsWith("V_VP8")) {
			return V_VP8;
		}
		else if(data.startsWith("A_AAC")) {
			return A_AAC;
		}
		else if(data.startsWith("A_MPEG")) {
			if(data.contains("L3")) {
				return A_MPEG_L3;
			}
		}
		else if(data.startsWith("A_VORBIS")) {
			return A_VORBIS;
		}
		throw new Exception("知らないCodec定義でした。:" + data);
	}
}
