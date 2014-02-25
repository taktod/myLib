package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * PixelWidthタグ
 * @author taktod
 */
public class PixelWidth extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public PixelWidth(EbmlValue size) {
		super(Type.PixelWidth, size);
	}
	/**
	 * コンストラクタ
	 */
	public PixelWidth() {
		this(new EbmlValue());
	}
}
