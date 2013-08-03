package com.ttProject.media.h264.frame;

import com.ttProject.media.h264.Frame;

/**
 * slice(通常のinnerFrame)
 * @author taktod
 *
 */
public class Slice extends Frame {
	public Slice(int size, byte frameTypeData) {
		super(size, frameTypeData);
	}
	public Slice(byte frameTypeData) {
		this(0, frameTypeData);
	}
}
