/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class Elst extends Atom {
	public Elst(int position, int size) {
		super(Elst.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		// この中身を解析する必要がある、解析しなかったら先頭のデータの同期がうまくとれなくなる。
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
