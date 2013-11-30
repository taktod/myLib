package com.ttProject.media.flv1.frame;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.flv1.Frame;

public class IntraFrame extends Frame {
	public IntraFrame(Bit8 pictureStartCode1, Bit8 pictureStartCode2, Bit1 pictureStartCode3,
			Bit5 version, Bit8 temporalReference, Bit3 pictureSize,
			int width, int height, Bit2 pictureType, Bit1 deblockingFlag,
			Bit5 quantizer, Bit1 extraInformationFlag, Bit8 extraInformation) {
		super(pictureStartCode1, pictureStartCode2, pictureStartCode3, version, temporalReference, pictureSize, width, height, pictureType, deblockingFlag, quantizer, extraInformationFlag, extraInformation);
	}
}
