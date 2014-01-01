package com.ttProject.container.mp4.stsd;

import com.ttProject.container.Reader;

/**
 * stsdの内部atomの解析動作
 * @author taktod
 */
public class StsdAtomReader extends Reader {
	/**
	 * コンストラクタ
	 */
	public StsdAtomReader() {
		super(new StsdAtomSelector());
	}
}
