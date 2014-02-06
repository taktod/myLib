package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SimpleTagタグ
 * @author taktod
 */
public class SimpleTag extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SimpleTag(EbmlValue size) {
		super(Type.SimpleTag, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
