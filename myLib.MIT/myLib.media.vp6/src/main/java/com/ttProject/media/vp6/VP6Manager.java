package com.ttProject.media.vp6;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.nio.channels.IReadChannel;

public class VP6Manager extends Manager<Frame> {
	@Override
	public Frame getUnit(IReadChannel source) throws Exception {
		return null;
	}
	@Override
	public List<Frame> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
}
