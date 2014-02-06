package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TrackEntryタグ
 * @author taktod
 */
public class TrackEntry extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TrackEntry(EbmlValue size) {
		super(Type.TrackEntry, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
