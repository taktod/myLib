package com.ttProject.frame.flv1;

import com.ttProject.frame.IVideoFrame;
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
public abstract class Frame implements IVideoFrame {
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
}
