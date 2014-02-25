package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * DefaultDurationタグ
 * @author taktod
 */
public class DefaultDuration extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DefaultDuration(EbmlValue size) {
		super(Type.DefaultDuration, size);
	}
	/**
	 * コンストラクタ
	 */
	public DefaultDuration() {
		this(new EbmlValue());
	}
}
