package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TagNameタグ
 * @author taktod
 */
public class TagName extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TagName(EbmlValue size) {
		super(Type.TagName, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
