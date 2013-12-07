package com.ttProject.container;

import com.ttProject.unit.IUnit;

/**
 * コンテナのベースとなるインターフェイス
 * @author taktod
 */
public interface IContainer extends IUnit {
	/**
	 * ファイル上のデータの開始位置
	 * @return
	 */
	public int getPosition();
}
