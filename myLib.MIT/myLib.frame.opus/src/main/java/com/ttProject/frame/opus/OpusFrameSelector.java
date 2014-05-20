package com.ttProject.frame.opus;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.opus.type.Frame;
import com.ttProject.frame.opus.type.HeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * フレーム選択
 * opusでは、headerFrame -> 実データという順にデータがはいっていると思われる
 * @author taktod
 */
public class OpusFrameSelector extends AudioSelector {
	/** header情報 */
	private HeaderFrame headerFrame = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		OpusFrame frame = null;
		// ここはきた順番ではなく、先頭がOpusTagsもしくはOpusHeadである場合は・・・で場合分けした方がよさそう。
		if(headerFrame == null) {
			frame = new HeaderFrame();
			headerFrame = (HeaderFrame)frame;
		}
		else {
			frame = new Frame();
			frame.setHeaderFrame(headerFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
