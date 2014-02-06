package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * BitDepthタグ
 * @author taktod
 */
public class BitDepth extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public BitDepth(EbmlValue size) {
		super(Type.BitDepth, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
