/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.field;

/**
 * interface of descriptor field object.
 * @author taktod
 */
public interface IDescriptorHolder {
	/**
	 * call in the case of data changed.
	 */
	public void updateSize();
}
