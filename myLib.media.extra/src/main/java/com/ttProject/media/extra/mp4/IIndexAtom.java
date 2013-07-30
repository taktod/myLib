package com.ttProject.media.extra.mp4;

import java.nio.channels.WritableByteChannel;

/**
 * version5のコンバート用につくったatomの共通動作
 * @author taktod
 */
public interface IIndexAtom {
	/**
	 * 一時ファイルにAtomデータを書き込む
	 * @param idx
	 * @throws Exception
	 */
	public void writeIndex(WritableByteChannel idx) throws Exception;
}
