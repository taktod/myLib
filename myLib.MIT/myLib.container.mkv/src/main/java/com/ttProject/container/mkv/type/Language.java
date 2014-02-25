package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvStringTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Languageタグ
 * @author taktod
 */
public class Language extends MkvStringTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Language(EbmlValue size) {
		super(Type.Language, size);
	}
	/**
	 * コンストラクタ
	 */
	public Language() {
		this(new EbmlValue());
	}
}
