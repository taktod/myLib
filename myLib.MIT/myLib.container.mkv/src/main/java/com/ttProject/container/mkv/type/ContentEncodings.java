package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ContentEncodingsタグ
 * @author taktod
 */
public class ContentEncodings extends MkvMasterTag {
	// contentEncodingを複数もっているらしい。
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentEncodings(EbmlValue size) {
		super(Type.ContentEncodings, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
