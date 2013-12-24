package com.ttProject.container;

import com.ttProject.unit.IUnit;

/**
 * コンテナのベースとなるインターフェイス
 * @author taktod
 */
public interface IContainer extends IUnit {
	/**
	 * IReadChannel上の位置情報参照(主にファイル上のデータの位置)
	 * @return
	 */
	public int getPosition();
}
