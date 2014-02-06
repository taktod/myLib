package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Cuesタグ
 * @author taktod
 */
public class Cues extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Cues(EbmlValue size) {
		super(Type.Cues, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
