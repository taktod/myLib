package com.ttProject.container.mkv;

import com.ttProject.container.IContainer;
import com.ttProject.container.Reader;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mkvデータを解析します。(内容データもばっちり解析する予定)
 * @author taktod
 */
public class MkvTagReader extends Reader {
	// TODO ここで必要なインスタンスを保持しておいて、参照できるようにする必要がある。
	// timescaleとかcodecTypeとかCodecPrivateとか・・・
	/**
	 * コンストラクタ
	 */
	public MkvTagReader() {
		super(new MkvTagSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		MkvTag tag = (MkvTag)getSelector().select(channel);
		if(tag != null) {
			tag.setMkvTagReader(this);
			tag.load(channel);
		}
		return tag;
	}
}
