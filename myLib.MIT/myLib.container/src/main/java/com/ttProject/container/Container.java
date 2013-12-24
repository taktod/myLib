package com.ttProject.container;

import com.ttProject.unit.Unit;

/**
 * コンテナの基本となるクラス
 * @author taktod
 */
public abstract class Container extends Unit implements IContainer {
	/** channel上の開始位置保持 */
	private int position;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPosition() {
		return position;
	}
	/**
	 * IReadChannel上の位置情報設定(主にfile上のデータの位置)
	 * @param position
	 */
	protected void setPosition(int position) {
		this.position = position;
	}
}
