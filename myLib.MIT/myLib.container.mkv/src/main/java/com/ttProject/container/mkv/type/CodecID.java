package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvStringTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CodecIDタグ
 * @author taktod
 */
public class CodecID extends MkvStringTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CodecID(EbmlValue size) {
		super(Type.CodecID, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
