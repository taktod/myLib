/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.adts;

import com.ttProject.container.Container;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * adtsのファイルユニット
 * @author taktod
 * TODO これもmp3と同じくちょっとおかしい。
 * 普通はunitを読み込んでからaacのフレームを取り出す方がただしいけど逆になっている。
 */
public class AdtsUnit extends Container {
	/** 音声Frame(aacのみ) */
	private final IAudioFrame frame;
	/**
	 * コンストラクタ
	 * @param frame
	 * @param position
	 * @param pts
	 */
	public AdtsUnit(AudioFrame frame, int position, long pts) {
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
	 * 内包フレーム参照
	 * @return
	 * @throws Exception
	 */
	public IAudioFrame getFrame() throws Exception {
		return frame;
	}
}
