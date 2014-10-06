/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * ContentCompAlgo
 * @author taktod
 */
public class ContentCompAlgo extends MkvUnsignedIntTag {
	/**
	 * constructor
	 * @param size
	 */
	public ContentCompAlgo(EbmlValue size) {
		super(Type.ContentCompAlgo, size);
	}
	/**
	 * constructor
	 */
	public ContentCompAlgo() {
		this(new EbmlValue());
	}
	/**
	 * ref the algotype.
	 * @return
	 * @throws Exception
	 */
	public Algo getType() throws Exception {
		return Algo.getType((int)getValue());
	}
	/**
	 * enum of algo
	 * @author taktod
	 */
	public static enum Algo {
		Zlib(0),
		@Deprecated
		Bzlib(1),
		@Deprecated
		Lzo1x(2),
		HeaderStripping(3);
		private final int value;
		private Algo(int value) {
			this.value = value;
		}
		public static Algo getType(int value) throws Exception {
			for(Algo t : values()) {
				if(t.value == value) {
					return t;
				}
			}
			throw new Exception("type is undecided.:" + value);
		}
	}
}
