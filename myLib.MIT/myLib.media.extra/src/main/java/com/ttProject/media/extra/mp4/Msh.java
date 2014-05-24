/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.extra.mp4;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mediaSequenceHeader用のatom
 * @author taktod
 */
public class Msh extends Atom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 */
	public Msh(int position, int size) {
		super("msh ", position, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer)
		throws Exception {
	}
}
