package com.ttProject.frame.flv1;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;

/**
 * flv1のframeのベース
 * @see http://hkpr.info/flash/swf/index.php?%E3%83%93%E3%83%87%E3%82%AA%2FSorenson%20H.263%20%E3%83%93%E3%83%83%E3%83%88%E3%82%B9%E3%83%88%E3%83%AA%E3%83%BC%E3%83%A0%E3%83%95%E3%82%A9%E3%83%BC%E3%83%9E%E3%83%83%E3%83%88
 * @author taktod
 */
public abstract class Flv1Frame implements IVideoFrame {
	@SuppressWarnings("unused")
	private Bit8 pictureStartCode1;
	@SuppressWarnings("unused")
	private Bit8 pictureStartCode2;
	@SuppressWarnings("unused")
	private Bit1 pictureStartCode3;
	private Bit5 version;
	private Bit8 temporalReference;
	@SuppressWarnings("unused")
	private Bit3 pictureSize;
	private int width;
	private int height;
	private Bit2 pictureType;
	private Bit1 deblockingFlag;
	private Bit5 quantizer;
	private Bit1 extraInformationFlag;
	private Bit8 extraInformation;
	private Bit extra; // 帳尻あわせよう
	public Flv1Frame(Bit8 pictureStartCode1, Bit8 pictureStartCode2, Bit1 pictureStartCode3,
			Bit5 version, Bit8 temporalReference, Bit3 pictureSize,
			int width, int height, Bit2 pictureType, Bit1 deblockingFlag,
			Bit5 quantizer, Bit1 extraInformationFlag, Bit8 extraInformation, Bit extra) {
		this.pictureStartCode1 = pictureStartCode1;
		this.pictureStartCode2 = pictureStartCode2;
		this.pictureStartCode3 = pictureStartCode3;
		this.version = version;
		this.temporalReference = temporalReference;
		this.pictureSize = pictureSize;
		this.width = width;
		this.height = height;
		this.pictureType = pictureType;
		this.deblockingFlag = deblockingFlag;
		this.quantizer = quantizer;
		this.extraInformationFlag = extraInformationFlag;
		this.extraInformation = extraInformation;
		this.extra = extra;
	}
	public Bit5 getVersion() {
		return version;
	}
	public Bit8 getTemporalReference() {
		return temporalReference;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public Bit2 getPictureType() {
		return pictureType;
	}
	public Bit1 getDeblockingFlag() {
		return deblockingFlag;
	}
	public Bit5 getQuantizer() {
		return quantizer;
	}
	public Bit1 getExtraInformationFlag() {
		return extraInformationFlag;
	}
	public Bit8 getExtraInformation() {
		return extraInformation;
	}
	@Override
	public long getPts() {
		return 0;
	}
	@Override
	public long getDts() {
		return 0;
	}
	@Override
	public long getTimebase() {
		return 0;
	}
	@Override
	public long getTimestamp() {
		return 0;
	}
}
