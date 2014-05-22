package com.ttProject.frame.opus;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.opus.type.CommentFrame;
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
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(OpusFrameSelector.class);
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
			String sigString = new String(BufferUtil.safeRead(channel, 7).array());
			if(sigString.equals("pusHead")) {
				// headerFrame
				frame = new HeaderFrame();
				headerFrame = (HeaderFrame)frame;
			}
			else if(sigString.equals("pusTags")) {
				// commentFrame
				frame = new CommentFrame();
			}
			else {
				throw new Exception("不明なフレームでした。:O" + sigString);
			}
		}
		else {
			// 普通のフレームである
			frame = new Frame(firstByte);
		}
		if(!(frame instanceof HeaderFrame)) {
			frame.setHeaderFrame(headerFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
