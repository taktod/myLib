package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Tagタグ
 * @author taktod
 */
public class Tag extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Tag(EbmlValue size) {
		super(Type.Tag, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
