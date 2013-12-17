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
	public void setHeaderFrame(HeaderFrame headerFrame) {
		this.headerFrame = headerFrame;
	}
	protected HeaderFrame getHeaderFrame() {
		return headerFrame;
	}
}
