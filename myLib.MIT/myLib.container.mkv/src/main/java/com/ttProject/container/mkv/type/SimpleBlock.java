package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SimpleBlockタグ
 * @author taktod
 */
public class SimpleBlock extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SimpleBlock(EbmlValue size) {
		super(Type.SimpleBlock, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
