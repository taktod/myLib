package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CuePointタグ
 * @author taktod
 */
public class CuePoint extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CuePoint(EbmlValue size) {
		super(Type.CuePoint, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
