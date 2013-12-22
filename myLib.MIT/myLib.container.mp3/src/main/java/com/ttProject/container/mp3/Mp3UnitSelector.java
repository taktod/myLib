package com.ttProject.container.mp3;

import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.frame.mp3.Mp3FrameSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * mp3のコンテナからmp3のunitを抜き出す動作
 * @author taktod
 *
 */
public class Mp3UnitSelector implements ISelector {
	/** 経過したサンプル数を保持しておく */
	private long passedTic = 0;
	private ISelector mp3FrameSelector = new Mp3FrameSelector();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// やることはmp3unitと同じだが、時間とかを計測しておかないとだめ。
		// mp3のframeを取得してからmp3Unitでwrapしておく。
		// 逆な気がするけど同じ解析なので、そうしたほうがはやい
		int position = channel.position();
		Mp3Frame frame = (Mp3Frame)mp3FrameSelector.select(channel);
		if(frame == null) {
			return null;
		}
		Mp3Unit unit = new Mp3Unit(frame, position, passedTic);
		passedTic += frame.getSampleNum();
		return unit;
	}
}
