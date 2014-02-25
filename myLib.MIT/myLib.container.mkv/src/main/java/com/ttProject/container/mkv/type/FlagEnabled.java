package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * FlagEnabledタグ
 * @author taktod
 */
public class FlagEnabled extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public FlagEnabled(EbmlValue size) {
		super(Type.FlagEnabled, size);
	}
	/**
	 * コンストラクタ
	 */
	public FlagEnabled() {
		this(new EbmlValue());
	}
}
