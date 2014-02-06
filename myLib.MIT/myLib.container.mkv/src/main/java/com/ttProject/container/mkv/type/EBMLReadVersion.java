package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * EBMLReadVersionタグ
 * @author taktod
 */
public class EBMLReadVersion extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBMLReadVersion(EbmlValue size) {
		super(Type.EBMLReadVersion, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
