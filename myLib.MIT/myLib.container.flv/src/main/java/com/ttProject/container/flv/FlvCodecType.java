/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

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
public enum FlvCodecType {
	NONE,
	// 映像用コーデック
	JPEG,FLV1,SCREEN,ON2VP6,ON2VP6_ALPHA,SCREEN_V2,H264,
	// 音声用コーデック
	PCM,ADPCM,MP3,LPCM,NELLY_16,NELLY_8,NELLY,G711_A,G711_U,RESERVED,AAC,SPEEX,MP3_8,DEVICE_SPECIFIC;
	/**
	 * 音声用コーデック判定
	 * @param tagByte
	 * @return
	 */
	public static FlvCodecType getAudioCodecType(int codecId) {
		switch(codecId) {
		case 0:  return PCM;
		case 1:  return ADPCM; // 1byte startPos? 2byte or 4byte 16bit?
		case 2:  return MP3;
		case 3:  return LPCM; // little endian pcm
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
	public static FlvCodecType getVideoCodecType(int codecId) {
		switch(codecId) {
		case 1: return JPEG;
		case 2: return FLV1;
		case 3: return SCREEN;
		case 4: return ON2VP6;
		case 5: return ON2VP6_ALPHA;
		case 6: return SCREEN_V2;
		case 7: return H264;

		case 0: // 不明
		default:
			throw new RuntimeException("判定不能なコーデック:" + codecId);
		}
	}
	/**
	 * コーデックデータから映像のtagByte用の数値を応答します。
	 * @param codec
	 * @return
	 * @throws Exception
	 */
	public static byte getVideoByte(FlvCodecType codec) throws Exception {
		switch(codec) {
		case JPEG:         return 1;
		case FLV1:         return 2;
		case SCREEN:       return 3;
		case ON2VP6:       return 4;
		case ON2VP6_ALPHA: return 5;
		case SCREEN_V2:    return 6;
		case H264:         return 7;
		case NONE:         return 0;
		default:
			throw new Exception("映像コーデックではありません。");
		}
	}
	/**
	 * コーデックtypeから数値を参照する。
	 * @param codec
	 * @return
	 * @throws Exception
	 */
	public static byte getVideoCodecNum(FlvCodecType codec) throws Exception {
		return getVideoByte(codec);
	}
	/**
	 * コーデックtypeから数値を参照する。
	 * @param codec
	 * @return
	 * @throws Exception
	 */
	public static byte getAudioCodecNum(FlvCodecType codec) throws Exception {
		switch(codec) {
		case PCM:             return 0;
		case ADPCM:           return 1;
		case MP3:             return 2;
		case LPCM:            return 3;
		case NELLY_16:        return 4;
		case NELLY_8:         return 5;
		case NELLY:           return 6;
		case G711_A:          return 7;
		case G711_U:          return 8;
		case RESERVED:        return 9;
		case AAC:             return 10;
		case SPEEX:           return 11;
		case MP3_8:           return 14;
		case DEVICE_SPECIFIC: return 15;
		default:
			throw new RuntimeException("判定不能なコーデック");
		}
	}
}
