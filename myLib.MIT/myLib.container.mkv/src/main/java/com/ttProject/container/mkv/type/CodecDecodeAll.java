package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CodecDecodeAllタグ
 * @author taktod
 */
public class CodecDecodeAll extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CodecDecodeAll(EbmlValue size) {
		super(Type.CodecDecodeAll, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
