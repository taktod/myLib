package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 映像フレームを解析する動作
 * @author taktod
 */
public abstract class VideoAnalyzer implements IAnalyzer {
	/** 映像解析のデータ選択オブジェクト */
	private final VideoSelector selector;
	/**
	 * コンストラクタ
	 * @param selector
	 */
	public VideoAnalyzer(VideoSelector selector) {
		this.selector = selector;
	}
	/**
	 * 映像解析セレクター参照
	 * @return
	 */
	public VideoSelector getSelector() {
		return selector;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		IFrame frame = (IFrame)selector.select(channel);
		if(frame != null) {
			frame.load(channel);
		}
		return frame;
	}
	@Override
	public IFrame getRemainFrame() throws Exception {
		return null;
	}
}
