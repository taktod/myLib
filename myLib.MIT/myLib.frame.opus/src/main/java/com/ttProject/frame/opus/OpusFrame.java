package com.ttProject.frame.opus;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.opus.type.HeaderFrame;

/**
 * opusのframe
 * @author taktod
 */
public abstract class OpusFrame extends AudioFrame {
	private HeaderFrame headerFrame = null;
	public void setHeaderFrame(HeaderFrame headerFrame) {
		this.headerFrame = headerFrame;
	}
	protected HeaderFrame getHeaderFrame() {
		return headerFrame;
	}
	public abstract boolean isComplete();
}
