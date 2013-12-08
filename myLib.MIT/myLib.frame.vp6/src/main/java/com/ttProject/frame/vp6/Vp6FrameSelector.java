package com.ttProject.frame.vp6;

import com.ttProject.frame.vp6.type.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

public class Vp6FrameSelector implements ISelector {
	/** 前回解析したkeyFrame情報は保持しておいて、interFrameに紐づける必要あり。 */
	private IntraFrame keyFrame = null;
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		return null;
	}
}
