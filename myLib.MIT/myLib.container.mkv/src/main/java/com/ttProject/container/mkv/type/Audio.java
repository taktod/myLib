package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Audioタグ
 * @author taktod
 */
public class Audio extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Audio(EbmlValue size) {
		super(Type.Audio, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
