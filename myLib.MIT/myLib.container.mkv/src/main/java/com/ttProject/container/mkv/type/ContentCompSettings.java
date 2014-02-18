package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ContentCompSettingsタグ
 * @author taktod
 */
public class ContentCompSettings extends MkvBinaryTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentCompSettings(EbmlValue size) {
		super(Type.ContentCompSettings, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
