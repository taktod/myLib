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
 * ContentCompAlgoタグ
 * @author taktod
 */
public class ContentCompAlgo extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public ContentCompAlgo(EbmlValue size) {
		super(Type.ContentCompAlgo, size);
	}
	/**
	 * コンストラクタ
	 */
	public ContentCompAlgo() {
		this(new EbmlValue());
	}
	/**
	 * 動作タイプを設定
	 * @return
	 * @throws Exception
	 */
	public Algo getType() throws Exception {
		return Algo.getType((int)getValue());
	}
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
			throw new Exception("typeが決定しませんでした:" + value);
		}
	}
}
