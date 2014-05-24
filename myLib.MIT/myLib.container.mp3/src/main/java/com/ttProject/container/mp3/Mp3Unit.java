/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp3;

import com.ttProject.container.Container;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp3のファイルユニット
 * @author taktod
 * TODO これちょっとおかしい。
 * 普通はunitを読み込んだあとにそこからframeを取り出すが、mp3の場合はframeありきになっている。
 */
public class Mp3Unit extends Container {
	/**
	 * mp3のframe
	 */
	private final IAudioFrame frame;
	/**
	 * コンストラクタ
	 * @param frame
	 * @param position
	 * @param pts
	 */
	public Mp3Unit(AudioFrame frame, int position, long pts) {
		this.frame = frame;
		setPosition(position);
		setPts(pts);
		setSize(frame.getSize());
		setTimebase(frame.getSampleRate());
		frame.setPts(pts);
		frame.setTimebase(frame.getSampleRate());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		frame.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		setData(frame.getData());
	}
	/**
	 * 保持フレーム参照
	 * @return
	 * @throws Exception
	 */
	public IAudioFrame getFrame() throws Exception {
		return frame;
	}
}
