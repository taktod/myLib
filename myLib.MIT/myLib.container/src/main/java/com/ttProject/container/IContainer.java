/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
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
