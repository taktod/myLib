package com.ttProject.unit;

import com.ttProject.nio.channels.IReadChannel;

/**
 * どのUnitであるかを調べる動作
 * データの中身の構築は実行しませんが、対象データがなんであるかを調査します。
 * h264のどのNalであるかとかを判定する。
 * @author taktod
 */
public interface ISelector {
	/**
	 * 解析動作 minimumloadまで実施して、必要な情報は集めますが、内部の詳細はチェックしません。高速動作したいときとかに使う。
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public IUnit select(IReadChannel channel) throws Exception;
}
