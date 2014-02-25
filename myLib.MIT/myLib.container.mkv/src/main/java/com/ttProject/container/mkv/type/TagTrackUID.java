package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TagTrackUIDタグ
 * @author taktod
 */
public class TagTrackUID extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TagTrackUID(EbmlValue size) {
		super(Type.TagTrackUID, size);
	}
	/**
	 * コンストラクタ
	 */
	public TagTrackUID() {
		this(new EbmlValue());
	}
}
