package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TagStringタグ
 * @author taktod
 */
public class TagString extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TagString(EbmlValue size) {
		super(Type.TagString, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
