package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TimecodeScaleタグ
 * @author taktod
 */
public class TimecodeScale extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TimecodeScale(EbmlValue size) {
		super(Type.TimecodeScale, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
