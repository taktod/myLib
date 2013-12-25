package com.ttProject.container.ogg;

import com.ttProject.container.Reader;

/**
 * oggのデータを解析する動作
 * @author taktod
 */
public class OggPageReader extends Reader {
	public OggPageReader() {
		super(new OggPageSelector());
	}
}
