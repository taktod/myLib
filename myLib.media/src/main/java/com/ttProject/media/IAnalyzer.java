package com.ttProject.media;

import com.ttProject.nio.channels.IReadChannel;

/**
 * unitの解析処理
 * @author taktod
 *
 */
public interface IAnalyzer<U> {
	/**
	 * 解析動作
	 * @param ch 読み込み対象チャンネル
	 * @return 解析後生成されるUnitエレメント
	 * @throws Exception 処理中に例外がでた場合の処理
	 */
	public U analyze(IReadChannel ch) throws Exception;
}
