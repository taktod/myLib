package com.ttProject.frame.speex;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.speex.type.HeaderFrame;

/**
 * speexのframe
 * @author taktod
 */
public abstract class SpeexFrame extends AudioFrame {
	/** headerデータを保持しておくことにする */
	private HeaderFrame headerFrame = null;
	/**
	 * headerFrameを設定する
	 * @param headerFrame
	 */
	public void setHeaderFrame(HeaderFrame headerFrame) {
		this.headerFrame = headerFrame;
		super.setBit(headerFrame.getBit());
		super.setChannel(headerFrame.getChannel());
		super.setSampleNum(headerFrame.getSampleNum());
		super.setSampleRate(headerFrame.getSampleRate());
	}
	/**
	 * headerFrameを参照する
	 * @return
	 */
	protected HeaderFrame getHeaderFrame() {
		return headerFrame;
	}
	public abstract boolean isComplete();
}
