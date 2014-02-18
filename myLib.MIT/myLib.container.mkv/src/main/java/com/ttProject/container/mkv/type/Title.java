package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Titleタグ
 * @author taktod
 */
public class Title extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Title(EbmlValue size) {
		super(Type.Title, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
