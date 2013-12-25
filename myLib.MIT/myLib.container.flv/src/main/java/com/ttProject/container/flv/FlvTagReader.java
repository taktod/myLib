package com.ttProject.container.flv;

import com.ttProject.container.Reader;

/**
 * flvデータを解析します。(内容データもばっちり解析します)
 * @author taktod
 */
public class FlvTagReader extends Reader {
	public FlvTagReader() {
		super(new FlvTagSelector());
	}
}
