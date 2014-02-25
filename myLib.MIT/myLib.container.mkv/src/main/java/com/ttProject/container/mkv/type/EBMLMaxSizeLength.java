package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * EBMLMaxSizeLengthタグ
 * @author taktod
 */
public class EBMLMaxSizeLength extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBMLMaxSizeLength(EbmlValue size) {
		super(Type.EBMLMaxSizeLength, size);
	}
	/**
	 * コンストラクタ
	 */
	public EBMLMaxSizeLength() {
		this(new EbmlValue());
	}
}
