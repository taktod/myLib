/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.vorbis.type.CommentHeaderFrame;
import com.ttProject.frame.vorbis.type.Frame;
import com.ttProject.frame.vorbis.type.IdentificationHeaderFrame;
import com.ttProject.frame.vorbis.type.SetupHeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * selector for vorbis frame.
 * @author taktod
 */
public class VorbisFrameSelector extends AudioSelector {
	// first need to read global information.
	private IdentificationHeaderFrame identificationHeaderFrame = null;
	private CommentHeaderFrame        commentHeaderFrame        = null;
	private SetupHeaderFrame          setupHeaderFrame          = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		VorbisFrame frame = null;
		if(identificationHeaderFrame == null) {
			identificationHeaderFrame = new IdentificationHeaderFrame();
			frame = identificationHeaderFrame;
		}
		else if(commentHeaderFrame == null) {
			commentHeaderFrame = new CommentHeaderFrame();
			identificationHeaderFrame.setCommentHeaderFrame(commentHeaderFrame);
			frame = commentHeaderFrame;
		}
		else if(setupHeaderFrame == null) {
			setupHeaderFrame = new SetupHeaderFrame();
			identificationHeaderFrame.setSetupHeaderFrame(setupHeaderFrame);
			frame = setupHeaderFrame;
		}
		else {
			frame = new Frame();
			frame.setIdentificationHeaderFrame(identificationHeaderFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
