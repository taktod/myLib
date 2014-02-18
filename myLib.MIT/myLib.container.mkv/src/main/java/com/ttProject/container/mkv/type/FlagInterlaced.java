package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * FlagInterlacedタグ
 * @author taktod
 */
public class FlagInterlaced extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public FlagInterlaced(EbmlValue size) {
		super(Type.FlagInterlaced, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
