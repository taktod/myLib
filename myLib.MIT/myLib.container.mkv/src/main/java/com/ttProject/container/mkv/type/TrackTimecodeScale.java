package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvFloatTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TrackTimecodeScaleタグ
 * このタグは非推奨です。
 * @author taktod
 */
public class TrackTimecodeScale extends MkvFloatTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TrackTimecodeScale(EbmlValue size) {
		super(Type.TrackTimecodeScale, size);
	}
	/**
	 * コンストラクタ
	 */
	public TrackTimecodeScale() {
		this(new EbmlValue());
	}
}
