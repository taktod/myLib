package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Channelsタグ
 * @author taktod
 */
public class Channels extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Channels(EbmlValue size) {
		super(Type.Channels, size);
	}
	/**
	 * コンストラクタ
	 */
	public Channels() {
		this(new EbmlValue());
	}
}
