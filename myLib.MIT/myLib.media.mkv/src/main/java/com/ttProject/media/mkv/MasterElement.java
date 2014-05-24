/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mkv;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.IAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public abstract class MasterElement extends Element {
	/** 子要素一覧 */
	private final List<Element> elements = new ArrayList<Element>();
	public MasterElement(Type type, long position, long size, long dataPosition) {
		super(type, position, size, dataPosition);
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
	public List<Element> getElements() {
		return new ArrayList<Element>(elements);
	}
}
