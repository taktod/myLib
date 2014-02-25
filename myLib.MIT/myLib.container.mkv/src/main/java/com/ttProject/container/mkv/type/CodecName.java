package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CodecNameタグ
 * @author taktod
 */
public class CodecName extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CodecName(EbmlValue size) {
		super(Type.CodecName, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
