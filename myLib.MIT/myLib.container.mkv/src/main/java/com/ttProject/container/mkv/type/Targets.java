package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Targetsタグ
 * @author taktod
 */
public class Targets extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Targets(EbmlValue size) {
		super(Type.Targets, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
