package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * PixelHeightタグ
 * @author taktod
 */
public class PixelHeight extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public PixelHeight(EbmlValue size) {
		super(Type.PixelHeight, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
