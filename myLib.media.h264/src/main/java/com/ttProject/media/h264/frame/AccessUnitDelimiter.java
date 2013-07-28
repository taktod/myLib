package com.ttProject.media.h264.frame;

import com.ttProject.media.h264.Frame;

/**
 * 00 00 00 01 [09 F0]
 * この部分だけになると思われる。
 * なおAccessUnitDelimiterは適当につくることが可能
 * @author taktod
 *
 */
public class AccessUnitDelimiter extends Frame {
	public AccessUnitDelimiter(byte frameTypeData) {
		this(0, frameTypeData);
	}
	public AccessUnitDelimiter(int size, byte frameTypeData) {
		super(size, frameTypeData);
	}
	public AccessUnitDelimiter() {
		this(0, (byte)0x09);
	}
}
