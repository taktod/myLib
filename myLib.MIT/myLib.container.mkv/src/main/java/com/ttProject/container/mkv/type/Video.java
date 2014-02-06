package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Videoタグ
 * @author taktod
 */
public class Video extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Video(EbmlValue size) {
		super(Type.Video, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
