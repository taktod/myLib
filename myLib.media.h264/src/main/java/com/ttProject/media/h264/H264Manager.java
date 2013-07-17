package com.ttProject.media.h264;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.Manager;
import com.ttProject.nio.channels.IReadChannel;

public class H264Manager extends Manager<Nal> {
	@Override
	public List<Nal> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
	@Override
	public Nal getUnit(IReadChannel source) throws Exception {
		// TODO nalをしるためには、次の00 00 01がでてくるところまでデータをコピーする必要があるのだが・・・
		// sizeを計算で出すことが不可能っぽいです。
		return null;
	}
}
