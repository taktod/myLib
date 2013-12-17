package com.ttProject.frame.speex;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.Frame;
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
		SpeexFrame frame = null;
		if(headerFrame == null) {
			// headerFrameであるとして処理する。
			frame = new HeaderFrame();
			headerFrame = (HeaderFrame)frame;
		}
		else if(commentFrame == null) {
			// commentFrameであるとして処理する。
			frame = new CommentFrame();
			commentFrame = (CommentFrame)frame;
		}
		else {
			// 通常のフレームとして処理する
			frame = new Frame();
		}
		frame.minimumLoad(channel);
		if(!(frame instanceof HeaderFrame)) {
			frame.setHeaderFrame(headerFrame);
		}
		return frame;
	}
}
