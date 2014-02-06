package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SeekHeadタグ
 * @author taktod
 */
public class SeekHead extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SeekHead(EbmlValue size) {
		super(Type.SeekHead, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
