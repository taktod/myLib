package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TimecodeScaleタグ
 * こっちは全体のtimescale値になるっぽい
 * これとは別にTrackTimecodeScaleというのもある(非推奨になってるけど)
 * @author taktod
 */
public class TimecodeScale extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TimecodeScale(EbmlValue size) {
		super(Type.TimecodeScale, size);
	}
//	public long getTimebaseValue() {
//		1000000000L / get
//	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
