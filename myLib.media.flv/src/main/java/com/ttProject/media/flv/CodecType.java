package com.ttProject.media.flv;

/**
 * コーデック処理
 * @author taktod
 * 
h263の場合のサイズをとる方法の部分、先頭の部分にデータがはいっているみたい・・・
00        00        84        00        81        40        00        B4        12
0000 0000 0000 0000 1000 0100 0000 0000 1000 0001 0100 0000 0000 0000 1011 0100 0001 0010 ...
DDD = 000の場合
AAAA AAAA AAAA AAAA ABBB BBCC CCCC CCDD DEEE EEEE EFFF FFFF FGGH IIII I
DDD = 001の場合
AAAA AAAA AAAA AAAA ABBB BBCC CCCC CCDD DEEE EEEE EEEE EEEE EFFF FFFF FFFF FFFF FGGH IIII I
DDDそれ以外の場合
AAAA AAAA AAAA AAAA ABBB BBCC CCCC CCDD DGGH IIII I

h263
http://hkpr.info/flash/swf/index.php?%E3%83%93%E3%83%87%E3%82%AA%2FSorenson%20H.263%20%E3%83%93%E3%83%83%E3%83%88%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%A0%E3%83%95%E3%82%A9%E3%83%BC%E3%83%9E%E3%83%83%E3%83%88

h264の場合nal構造のspsをみればいいみたい。

AACの情報
http://jongyeob.com/moniwiki/pds/upload/13818-7.pdf
http://ee72078.moo.jp/chinsan/pc/Lab/index.php?AAC
http://blog-imgs-18-origin.fc2.com/n/a/n/nanncyatte/aacfileheader.png


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
		switch((tagByte & 0xFF) >>> 4) {
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
	/**
	 * コーデックデータから映像のtagByte用の数値を応答します。
	 * @param codec
	 * @return
	 * @throws Exception
	 */
	public static byte getVideoByte(CodecType codec) throws Exception {
		switch(codec) {
		case JPEG:         return 1;
		case H263:         return 2;
		case SCREEN:       return 3;
		case ON2VP6:       return 4;
		case ON2VP6_ALPHA: return 5;
		case SCREEN_V2:    return 6;
		case AVC:          return 7;
		case NONE:         return 0;
		default:
			throw new Exception("映像コーデックではありません。");
		}
	}
}
