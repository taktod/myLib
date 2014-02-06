package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * DocTypeReadVersionタグ
 * @author taktod
 */
public class DocTypeReadVersion extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DocTypeReadVersion(EbmlValue size) {
		super(Type.DocTypeReadVersion, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
