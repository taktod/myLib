package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CodecPrivateタグ
 * @author taktod
 */
public class CodecPrivate extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CodecPrivate(EbmlValue size) {
		super(Type.CodecPrivate, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}