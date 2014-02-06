package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * DocTypeVersionタグ
 * @author taktod
 */
public class DocTypeVersion extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DocTypeVersion(EbmlValue size) {
		super(Type.DocTypeVersion, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
