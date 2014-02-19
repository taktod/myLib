package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * BlockDurationタグ
 * @author taktod
 */
public class BlockDuration extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public BlockDuration(EbmlValue size) {
		super(Type.BlockDuration, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
