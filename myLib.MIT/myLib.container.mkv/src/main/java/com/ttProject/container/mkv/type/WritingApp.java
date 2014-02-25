package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUtf8Tag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * WritingAppタグ
 * @author taktod
 */
public class WritingApp extends MkvUtf8Tag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public WritingApp(EbmlValue size) {
		super(Type.WritingApp, size);
	}
	/**
	 * コンストラクタ
	 */
	public WritingApp() {
		this(new EbmlValue());
	}
}
