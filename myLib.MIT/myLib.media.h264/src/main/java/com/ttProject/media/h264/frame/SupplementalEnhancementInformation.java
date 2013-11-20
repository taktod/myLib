package com.ttProject.media.h264.frame;

import com.ttProject.media.h264.Frame;

public class SupplementalEnhancementInformation extends Frame {
	public SupplementalEnhancementInformation(int size, byte frameTypeData) {
		super(size, frameTypeData);
	}
	public SupplementalEnhancementInformation(byte frameTypeData) {
		this(0, frameTypeData);	
	}
}
