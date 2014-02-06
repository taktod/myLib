package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * MuxingAppタグ
 * @author taktod
 */
public class MuxingApp extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public MuxingApp(EbmlValue size) {
		super(Type.MuxingApp, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
