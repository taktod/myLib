package com.ttProject.container.mkv.type;


import com.ttProject.container.mkv.MkvSignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ReferenceBlockタグ
 * @author taktod
 */
public class ReferenceBlock extends MkvSignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ReferenceBlock(EbmlValue size) {
		super(Type.ReferenceBlock, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
