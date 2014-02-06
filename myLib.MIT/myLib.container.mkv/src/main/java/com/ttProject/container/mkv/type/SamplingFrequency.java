package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvFloatTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * SamplingFrequencyタグ
 * @author taktod
 */
public class SamplingFrequency extends MkvFloatTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SamplingFrequency(EbmlValue size) {
		super(Type.SamplingFrequency, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
