package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Blockタグ
 * @author taktod
 */
public class Block extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Block(EbmlValue size) {
		super(Type.Block, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
