/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import com.ttProject.unit.Unit;

/**
 * basic for container
 * @author taktod
 */
public abstract class Container extends Unit implements IContainer {
	/** position on the IReadChannel */
	private int position;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPosition() {
		return position;
	}
	/**
	 * set the container element position on the channel.
	 * @param position
	 */
	protected void setPosition(int position) {
		this.position = position;
	}
}
