package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CRC32タグ
 * @author taktod
 */
public class CRC32 extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CRC32(EbmlValue size) {
		super(Type.CRC32, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
