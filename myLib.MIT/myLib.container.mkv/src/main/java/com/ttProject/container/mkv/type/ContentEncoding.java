package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ContentEncodingタグ
 * @author taktod
 */
public class ContentEncoding extends MkvMasterTag {
	// compressionかencryptionのどちらかを１つだけもってるっぽい。
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentEncoding(EbmlValue size) {
		super(Type.ContentEncoding, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
