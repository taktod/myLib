package com.ttProject.frame.opus;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.opus.type.Frame;
import com.ttProject.frame.opus.type.HeaderFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

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
		// 1文字目をみて、OだったらOpusである可能性があるとする(いきなり８文字だと、それ以下のデータ量であることがあるっぽい。)
		byte firstByte = BufferUtil.safeRead(channel, 1).get();
		if(firstByte == 'O' && channel.size() > 8) {
			// のこり7文字も読み込んでOpusHead or OpusTagsであるか確認する。
		}
		else {
			// 普通のフレームである
			frame = new Frame();
		}
/*		if(headerFrame == null) {
			frame = new HeaderFrame();
			headerFrame = (HeaderFrame)frame;
		}
		else {
			frame = new Frame();
			frame.setHeaderFrame(headerFrame);
		}*/
		frame.minimumLoad(channel);
		return frame;
	}
}
