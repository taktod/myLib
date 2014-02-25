package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CueClusterPositionタグ
 * @author taktod
 */
public class CueClusterPosition extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CueClusterPosition(EbmlValue size) {
		super(Type.CueClusterPosition, size);
	}
	/**
	 * コンストラクタ
	 */
	public CueClusterPosition() {
		this(new EbmlValue());
	}
}
