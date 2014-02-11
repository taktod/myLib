package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Segmentタグ
 * @author taktod
 */
public class Segment extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Segment(EbmlValue size) {
		super(Type.Segment, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.load(channel);
	}
}
