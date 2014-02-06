package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Tagsタグ
 * @author taktod
 */
public class Tags extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Tags(EbmlValue size) {
		super(Type.Tags, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
