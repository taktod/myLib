package com.ttProject.frame.mjpeg;

import com.ttProject.frame.VideoSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * 
 * @author taktod
 */
public class MjpegFrameSelector extends VideoSelector {
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// channelから必要なデータを取り出したい。
		throw new Exception("データ作成が未実装");
	}
}
