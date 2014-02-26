package com.ttProject.container.mkv.type;


import com.ttProject.container.mkv.MkvDateTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * DateUTCタグ
 * @author taktod
 */
public class DateUTC extends MkvDateTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public DateUTC(EbmlValue size) {
		super(Type.DateUTC, size);
	}
	/**
	 * コンストラクタ
	 */
	public DateUTC() {
		this(new EbmlValue());
	}
}
