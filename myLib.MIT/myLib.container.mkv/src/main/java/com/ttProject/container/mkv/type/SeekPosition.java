package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SeekPositionタグ
 * @author taktod
 */
public class SeekPosition extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SeekPosition(EbmlValue size) {
		super(Type.SeekPosition, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
