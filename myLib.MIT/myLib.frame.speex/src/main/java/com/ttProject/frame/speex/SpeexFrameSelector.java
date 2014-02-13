package com.ttProject.frame.speex;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.Frame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * speexフレーム選択
 * speexでは、headerFrame -> commentFrame -> 実データという順にデータがはいるらしい
 * @author taktod
 */
public class SpeexFrameSelector extends AudioSelector {
	/** header情報 */
	private HeaderFrame headerFrame = null;
	/** コメント情報(メタデータなので、とりあえず捨てておく) */
	private CommentFrame commentFrame = null;
	/**
	 * headerフレーム設定(flvで使う)
	 * @param frame
	 */
	public void setHeaderFrame(HeaderFrame frame) {
		this.headerFrame = frame;
	}
	/**
	 * commentフレーム設定(flvで設定する)
	 * @param frame
	 */
	public void setCommentFrame(CommentFrame frame) {
		this.commentFrame = frame;
	}
	/**
	 * {@inheritDoc}
	 */
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
			frame.setHeaderFrame(headerFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
