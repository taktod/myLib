package com.ttProject.container.mkv;

import com.ttProject.container.Reader;

/**
 * mkvデータを解析します。(内容データもばっちり解析する予定)
 * @author taktod
 */
public class MkvElementReader extends Reader {
	public MkvElementReader() {
		super(new MkvElementSelector());
	}
}
