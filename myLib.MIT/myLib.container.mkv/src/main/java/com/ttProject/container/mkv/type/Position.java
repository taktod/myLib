package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Positionタグ
 * @author taktod
 */
public class Position extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Position(EbmlValue size) {
		super(Type.Position, size);
	}
	/**
	 * コンストラクタ
	 */
	public Position() {
		this(new EbmlValue());
	}
}
