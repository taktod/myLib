package com.ttProject.container.riff;

import com.ttProject.frame.CodecType;

/**
 * RiffCodecType def, for strh.
 * @author taktod
 */
public enum StrhRiffCodecType {
	MPEG(0x4D504547, CodecType.UNKNOWN_VIDEO),
	MPG1(0x4D504731, CodecType.UNKNOWN_VIDEO),
	MPG2(0x4D504732, CodecType.UNKNOWN_VIDEO),
	MP4V(0x4D503456, CodecType.UNKNOWN_VIDEO),
	XVID(0x58564944, CodecType.UNKNOWN_VIDEO),
	DX50(0x44583530, CodecType.UNKNOWN_VIDEO),
	DIVX(0x44495658, CodecType.UNKNOWN_VIDEO),
	DIV5(0x44495635, CodecType.UNKNOWN_VIDEO),
	DIV4(0x44495634, CodecType.UNKNOWN_VIDEO),
	_3IVX(0x33495658, CodecType.UNKNOWN_VIDEO),
	_3IV2(0x33495632, CodecType.UNKNOWN_VIDEO),
	RMP4(0x524D5034, CodecType.UNKNOWN_VIDEO),
	MPG4(0x4D504734, CodecType.UNKNOWN_VIDEO),
	MP42(0x4D503432, CodecType.UNKNOWN_VIDEO),
	MP43(0x4D503433, CodecType.UNKNOWN_VIDEO),
	WMV1(0x574D5631, CodecType.UNKNOWN_VIDEO),
	WMV2(0x574D5632, CodecType.UNKNOWN_VIDEO),
	WMV3(0x574D5633, CodecType.UNKNOWN_VIDEO),
	DVSD(0x44565344, CodecType.UNKNOWN_VIDEO),
	DVIS(0x44564953, CodecType.UNKNOWN_VIDEO),
	FLV1(0x464C5631, CodecType.FLV1),
	FLV4(0x464C5634, CodecType.UNKNOWN_VIDEO),
	MJPG(0x4D4A5047, CodecType.MJPEG),
	LJPG(0x4C4A5047, CodecType.UNKNOWN_VIDEO),
	AVC1(0x41564331, CodecType.UNKNOWN_VIDEO),
	DAVC(0x44415643, CodecType.UNKNOWN_VIDEO),
	H264(0x48323634, CodecType.H264),
	X264(0x58323634, CodecType.UNKNOWN_VIDEO),
	H263(0x48323633, CodecType.UNKNOWN_VIDEO),
	S263(0x53323633, CodecType.UNKNOWN_VIDEO),
	H261(0x48323631, CodecType.UNKNOWN_VIDEO),
	FFV1(0x46465631, CodecType.UNKNOWN_VIDEO),
	HFYU(0x48465955, CodecType.UNKNOWN_VIDEO),
	FFVH(0x46465648, CodecType.UNKNOWN_VIDEO),
	ZLIB(0x5A4C4942, CodecType.UNKNOWN_VIDEO),
	MSZH(0x4D535A48, CodecType.UNKNOWN_VIDEO),
	THEO(0x5448454F, CodecType.UNKNOWN_VIDEO),
	theo(0x7468656F, CodecType.THEORA),
	IV31(0x49563331, CodecType.UNKNOWN_VIDEO),
	IV32(0x49563332, CodecType.UNKNOWN_VIDEO),
	CVID(0x43564944, CodecType.UNKNOWN_VIDEO),
	CRAM(0x4352414D, CodecType.UNKNOWN_VIDEO),
	VP30(0x56503330, CodecType.UNKNOWN_VIDEO),
	VP31(0x56503331, CodecType.UNKNOWN_VIDEO),
	VP40(0x56503430, CodecType.UNKNOWN_VIDEO),
	VP50(0x56503530, CodecType.UNKNOWN_VIDEO),
	VP60(0x56503630, CodecType.UNKNOWN_VIDEO),
	VP61(0x56503631, CodecType.UNKNOWN_VIDEO),
	VP62(0x56503632, CodecType.UNKNOWN_VIDEO),
	VP70(0x56503730, CodecType.UNKNOWN_VIDEO),
	VP80(0x56503830, CodecType.UNKNOWN_VIDEO),
	WVC1(0x57564331, CodecType.UNKNOWN_VIDEO),
	PCM(0x00000000, CodecType.UNKNOWN_AUDIO), // like pcm? waveFormatEx will have details.
	ADPCM(0x01000000, CodecType.UNKNOWN_AUDIO); // adpcm? waveFormatEx will have details.
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
