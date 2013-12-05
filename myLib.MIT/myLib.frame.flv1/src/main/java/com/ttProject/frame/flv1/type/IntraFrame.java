package com.ttProject.frame.flv1.type;

import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;

public class IntraFrame extends Flv1Frame {
	public IntraFrame(Bit8 pictureStartCode1, Bit8 pictureStartCode2, Bit1 pictureStartCode3,
			Bit5 version, Bit8 temporalReference, Bit3 pictureSize,
			int width, int height, Bit2 pictureType, Bit1 deblockingFlag,
			Bit5 quantizer, Bit1 extraInformationFlag, Bit8 extraInformation, Bit extra) {
		super(pictureStartCode1, pictureStartCode2, pictureStartCode3,
				version, temporalReference, pictureSize, 
				width, height, pictureType, deblockingFlag,
				quantizer, extraInformationFlag, extraInformation, extra);
	}
}
