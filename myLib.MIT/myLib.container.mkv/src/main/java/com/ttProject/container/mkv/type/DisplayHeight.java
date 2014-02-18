package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * DisplayHeightタグ
 * @author taktod
 */
public class DisplayHeight extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DisplayHeight(EbmlValue size) {
		super(Type.DisplayHeight, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
