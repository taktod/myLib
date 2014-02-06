package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * CueTrackPositionsタグ
 * @author taktod
 */
public class CueTrackPositions extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public CueTrackPositions(EbmlValue size) {
		super(Type.CueTrackPositions, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
