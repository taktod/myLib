package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * EBMLVersionタグ
 * @author taktod
 */
public class EBMLVersion extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBMLVersion(EbmlValue size) {
		super(Type.EBMLVersion, size);
	}
	public EBMLVersion() {
		super(Type.EBMLVersion, new EbmlValue());
	}
}
