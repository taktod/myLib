/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import com.ttProject.unit.IUnit;

/**
 * interface of container base.
 * @author taktod
 */
public interface IContainer extends IUnit {
	/**
	 * ref the container element position on the channel.
	 * @return
	 */
	public int getPosition();
}
