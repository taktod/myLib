package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Tracksタグ
 * @author taktod
 */
public class Tracks extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Tracks(EbmlValue size) {
		super(Type.Tracks, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
