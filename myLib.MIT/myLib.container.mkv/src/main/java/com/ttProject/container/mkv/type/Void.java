package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Voidタグ
 * @author taktod
 *
 */
public class Void extends MkvTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Void(EbmlValue size) {
		super(Type.Void, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
