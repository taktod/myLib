package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CueTrackタグ
 * @author taktod
 */
public class CueTrack extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CueTrack(EbmlValue size) {
		super(Type.CueTrack, size);
	}
	/**
	 * コンストラクタ
	 */
	public CueTrack() {
		this(new EbmlValue());
	}
}
