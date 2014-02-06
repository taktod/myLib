package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Seekタグ
 * @author taktod
 */
public class Seek extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Seek(EbmlValue size) {
		super(Type.Seek, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
