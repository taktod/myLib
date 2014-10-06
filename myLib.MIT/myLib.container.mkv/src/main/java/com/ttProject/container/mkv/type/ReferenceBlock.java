/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;


import com.ttProject.container.mkv.MkvSignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ReferenceBlock
 * @author taktod
 */
public class ReferenceBlock extends MkvSignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public ReferenceBlock(EbmlValue size) {
		super(Type.ReferenceBlock, size);
	}
	/**
	 * constructor
	 */
	public ReferenceBlock() {
		this(new EbmlValue());
	}
}
