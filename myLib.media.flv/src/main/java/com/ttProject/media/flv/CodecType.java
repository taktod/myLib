package com.ttProject.media.flv;

/**
 * コーデック処理
 * @author taktod
 */
public enum CodecType {
	NONE,
	// 映像用コーデック
	JPEG,H263,SCREEN,ON2VP6,ON2VP6_ALPHA,SCREEN_V2,AVC,
	// 音声用コーデック
	ADPCM,MP3,PCM,NELLY_16,NELLY_8,NELLY,G711_A,G711_U,RESERVED,AAC,SPEEX,MP3_8,DEVICE_SPECIFIC;
	/**
	 * 音声用コーデック判定
	 * @param tagByte
	 * @return
	 */
	public static CodecType getAudioCodecType(byte tagByte) {
		switch((tagByte &0xFF) >>> 4) {
		case 0:  return PCM;
		case 1:  return ADPCM;
		case 2:  return MP3;
		case 3:  return PCM;
		case 4:  return NELLY_16;
		case 5:  return NELLY_8;
		case 6:  return NELLY;
		case 7:  return G711_A;
		case 8:  return G711_U;
		case 9:  return RESERVED;
		case 10: return AAC;
		case 11: return SPEEX;
		case 14: return MP3_8;
		case 15: return DEVICE_SPECIFIC;

		case 12: // 不明
		case 13: // 未定義
		default:
			throw new RuntimeException("判定不能なコーデック");
		}
	}
	/**
	 * 映像用コーデック判定
	 * @param tagByte
	 * @return
	 */
	public static CodecType getVideoCodecType(byte tagByte) {
		switch(tagByte & 0x0F) {
		case 1: return JPEG;
		case 2: return H263;
		case 3: return SCREEN;
		case 4: return ON2VP6;
		case 5: return ON2VP6_ALPHA;
		case 6: return SCREEN_V2;
		case 7: return AVC;

		case 0: // 不明
		default:
			throw new RuntimeException("判定不能なコーデック");
		}
	}
}
