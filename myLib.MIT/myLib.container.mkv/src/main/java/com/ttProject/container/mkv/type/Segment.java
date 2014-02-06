package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
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
}
