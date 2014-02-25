package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * FlagForcedタグ
 * @author taktod
 */
public class FlagForced extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public FlagForced(EbmlValue size) {
		super(Type.FlagForced, size);
	}
	/**
	 * コンストラクタ
	 */
	public FlagForced() {
		this(new EbmlValue());
	}
}
