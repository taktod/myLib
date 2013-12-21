package com.ttProject.frame.flv1.type;

import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.bit.Bit17;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * スルー可能な中間フレーム
 * @author taktod
 */
public class DisposableInterFrame extends Flv1Frame {
	public DisposableInterFrame(Bit17 pictureStartCode,
			Bit5 version, Bit8 temporalReference, Bit3 pictureSize,
			Bit customWidth, Bit customHeight,
			int width, int height, Bit2 pictureType, Bit1 deblockingFlag,
			Bit5 quantizer, Bit1 extraInformationFlag, Bit8 extraInformation, Bit extra) {
		super(pictureStartCode,
				version, temporalReference, pictureSize, 
				customWidth, customHeight,
				width, height, pictureType, deblockingFlag,
				quantizer, extraInformationFlag, extraInformation, extra);
	}
}
