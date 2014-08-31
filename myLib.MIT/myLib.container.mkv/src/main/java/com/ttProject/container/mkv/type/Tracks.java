/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Tracksタグ
 * @author taktod
 */
public class Tracks extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Tracks(EbmlValue size) {
		super(Type.Tracks, size);
	}
	/**
	 * コンストラクタ
	 */
	public Tracks() {
		this(new EbmlValue());
	}
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Tracks(long position) {
		this();
		setPosition((int)position);
	}
}
