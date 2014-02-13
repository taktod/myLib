package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.vorbis.type.CommentHeaderFrame;
import com.ttProject.frame.vorbis.type.Frame;
import com.ttProject.frame.vorbis.type.IdentificationHeaderFrame;
import com.ttProject.frame.vorbis.type.SetupHeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * vorbisのframeを読み込みます。
 * 
 * とりあえずspeexと同じ方法でやっていきます。
 * @author taktod
 */
public class VorbisFrameSelector extends AudioSelector {
	// 先にframe情報を順番に読み込む
	private IdentificationHeaderFrame identificationHeaderFrame = null;
	private CommentHeaderFrame        commentHeaderFrame        = null;
	private SetupHeaderFrame          setupHeaderFrame          = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
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
