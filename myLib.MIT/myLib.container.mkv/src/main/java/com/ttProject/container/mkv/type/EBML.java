package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * EBMLタグ
 * @author taktod
 */
public class EBML extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBML(EbmlValue size) {
		super(Type.EBML, size);
	}
	/**
	 * コンストラクタ
	 */
	public EBML() {
		super(Type.EBML, new EbmlValue());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// 中の子要素を確認して、sizeを決定する必要がある。
	}
}
