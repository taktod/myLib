package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * EBMLMaxIDLengthタグ
 * @author taktod
 */
public class EBMLMaxIDLength extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBMLMaxIDLength(EbmlValue size) {
		super(Type.EBMLMaxIDLength, size);
	}
	/**
	 * コンストラクタ
	 */
	public EBMLMaxIDLength() {
		this(new EbmlValue());
	}
}
