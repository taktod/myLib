package com.ttProject.frame.speex;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

public class SpeexFrameSelector extends AudioSelector {
	private HeaderFrame headerFrame = null;
	private CommentFrame commentFrame = null;
	public void setHeaderFrame(HeaderFrame frame) {
		this.headerFrame = frame;
	}
	public void setCommentFrame(CommentFrame frame) {
		this.commentFrame = frame;
	}
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(headerFrame == null) {
			// headerFrameであるとして処理する。
		}
		else if(commentFrame == null) {
			// commentFrameであるとして処理する。
		}
		else {
			// 通常のフレームとして処理する
		}
		return null;
	}
}
