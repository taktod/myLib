package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CueTimeタグ
 * @author taktod
 */
public class CueTime extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CueTime(EbmlValue size) {
		super(Type.CueTime, size);
	}
	/**
	 * コンストラクタ
	 */
	public CueTime() {
		this(new EbmlValue());
	}
}
