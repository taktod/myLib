package com.ttProject.media;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * unitの解析処理
 * @author taktod
 *
 */
public interface IUnitAnalyzer {
	/**
	 * 解析動作
	 * @param ch 読み込み対象チャンネル
	 * @return 解析後生成されるUnitエレメント
	 * @throws Exception 処理中に例外がでた場合の処理
	 */
	public Unit analyze(IFileReadChannel ch) throws Exception;
}
