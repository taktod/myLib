package com.ttProject.frame.nellymoser;

import com.ttProject.frame.nellymoser.type.Frame;
import com.ttProject.frame.nellymoser.type.MultiFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * nellymoserのframe選択
 * @author taktod
 *
 */
public class NellymoserFrameSelector implements ISelector {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// frameをいくつ保持しているか確認する。
		if(channel.size() % 64 != 0) {
			throw new Exception("保持チャンネル数がおかしいです。");
		}
		int count = channel.size() / 64;
		if(count != 1) {
			MultiFrame frame = new MultiFrame();
			for(int i = 0;i < count;i ++) {
				NellymoserFrame innerFrame = new Frame();
				innerFrame.minimumLoad(channel);
				innerFrame.load(channel); // minimumloadだけですべてとれないので、通常のloadも実行してしまいます。
				frame.add(innerFrame);
			}
			frame.minimumLoad(channel);
			return frame;
		}
		else {
			NellymoserFrame frame = new Frame();
			frame.minimumLoad(channel);
			return frame;
		}
	}
}
