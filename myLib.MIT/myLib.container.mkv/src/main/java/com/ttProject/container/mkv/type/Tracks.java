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
 * Tracks
 * @author taktod
 */
public class Tracks extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public Tracks(EbmlValue size) {
		super(Type.Tracks, size);
	}
	/**
	 * constructor
	 */
	public Tracks() {
		this(new EbmlValue());
	}
	/**
	 * constructor
	 * @param position
	 */
	public Tracks(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * set the position.
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
}
