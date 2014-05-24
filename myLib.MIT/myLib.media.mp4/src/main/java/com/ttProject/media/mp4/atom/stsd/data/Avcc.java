/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4.atom.stsd.data;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * h.264のmediaSequenceHeaderを保持しているデータ
 * 動画用(中身全部がそのままmediaSequenceHeaderになる)
 * @author taktod
 */
public class Avcc extends Atom {
	public Avcc(int position, int size) {
		super(Avcc.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
}
