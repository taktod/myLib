package com.ttProject.container;

import com.ttProject.unit.Unit;

/**
 * コンテナの基本となるクラス
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
	protected void setPosition(int position) {
		this.position = position;
	}
}
