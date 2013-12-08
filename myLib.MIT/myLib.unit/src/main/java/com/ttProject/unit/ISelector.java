package com.ttProject.unit;

import com.ttProject.nio.channels.IReadChannel;

/**
 * どのUnitであるかを調べる動作
 * データの中身の構築は実行しませんが、対象データがなんであるかを調査します。
 * h264のどのNalであるかとかを判定する。
 * @author taktod
 */
public interface ISelector {
	public IUnit select(IReadChannel channel) throws Exception;
}