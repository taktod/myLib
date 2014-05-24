/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.field;

/**
 * descriptorを保持しているfieldのinterface
 * @author taktod
 */
public interface IDescriptorHolder {
	/**
	 * descriptorのデータが変更されたときに呼び出される動作
	 */
	public void updateSize();
}
