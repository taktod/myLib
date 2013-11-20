package com.ttProject.media.mkv;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.nio.channels.IReadChannel;

public class MkvManager extends Manager<Element> {
	@Override
	public List<Element> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
	@Override
	public Element getUnit(IReadChannel source) throws Exception {
		// unitを解析します。
		System.out.println("elementを解析するよん。");
		return null;
	}
}
