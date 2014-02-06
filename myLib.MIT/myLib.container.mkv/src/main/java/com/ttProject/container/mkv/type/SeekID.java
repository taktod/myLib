package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SeekIDタグ
 * @author taktod
 */
public class SeekID extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SeekID(EbmlValue size) {
		super(Type.SeekID, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
