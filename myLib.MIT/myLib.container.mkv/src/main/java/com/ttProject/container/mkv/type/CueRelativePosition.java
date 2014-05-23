package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CueRelativePositionタグ
 * @author taktod
 */
public class CueRelativePosition extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CueRelativePosition(EbmlValue size) {
		super(Type.CueRelativePosition, size);
	}
	/**
	 * コンストラクタ
	 */
	public CueRelativePosition() {
		this(new EbmlValue());
	}
}
