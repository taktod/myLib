/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.theora.type.CommentHeaderFrame;
import com.ttProject.frame.theora.type.IdentificationHeaderDecodeFrame;
import com.ttProject.frame.theora.type.SetupHeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * theoraのフレーム解析を実施します。
 * @author taktod
 */
public class TheoraFrameSelector extends AudioSelector {
	// 先にこれらのデータを読み込む必要あり
	private IdentificationHeaderDecodeFrame identificationHeaderDecodeFrame = null;
	private CommentHeaderFrame commentHeaderFrame = null;
	private SetupHeaderFrame setupHeaderFrame = null;
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		TheoraFrame frame = null;
		if(identificationHeaderDecodeFrame == null) {
			identificationHeaderDecodeFrame = new IdentificationHeaderDecodeFrame();
			frame = identificationHeaderDecodeFrame;
		}
		else if(commentHeaderFrame == null) {
			commentHeaderFrame = new CommentHeaderFrame();
			identificationHeaderDecodeFrame.setCommentHeaderFrame(commentHeaderFrame);
			frame = commentHeaderFrame;
		}
		else if(setupHeaderFrame == null) {
			setupHeaderFrame = new SetupHeaderFrame();
			identificationHeaderDecodeFrame.setSetupHeaderFrame(setupHeaderFrame);
			frame = setupHeaderFrame;
		}
		else {
//			frame = new Frame();
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
