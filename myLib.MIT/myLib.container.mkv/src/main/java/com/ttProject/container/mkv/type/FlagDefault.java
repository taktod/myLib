package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * FlagDefaultタグ
 * @author taktod
 */
public class FlagDefault extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public FlagDefault(EbmlValue size) {
		super(Type.FlagDefault, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
