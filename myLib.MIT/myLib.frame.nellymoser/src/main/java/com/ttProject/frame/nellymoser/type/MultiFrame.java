package com.ttProject.frame.nellymoser.type;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 複数のフレームを保持している場合
 * flvのnellymoserは複数のフレームを保持することがあります。
 * @author taktod
 */
public class MultiFrame extends NellymoserFrame {
	/** 内包フレーム */
	private List<NellymoserFrame> frameList = new ArrayList<NellymoserFrame>();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// このタイミングで必要な情報をあつめておく。
		// といってもサンプル数とかしかとれなさそうだけど。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
	public void add(NellymoserFrame frame) {
		frameList.add(frame);
	}
}
