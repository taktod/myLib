package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Infoタグ
 * @author taktod
 */
public class Info extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Info(EbmlValue size) {
		super(Type.Info, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
