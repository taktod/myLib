package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * EBMLタグ
 * @author taktod
 */
public class EBML extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBML(EbmlValue size) {
		super(Type.EBML, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
