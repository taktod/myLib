package com.ttProject.frame.flv1.type;

import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitN.Bit17;

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
