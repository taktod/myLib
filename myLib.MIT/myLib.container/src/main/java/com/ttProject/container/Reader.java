/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;

/**
 * base for reader.
 * @author taktod
 */
public abstract class Reader implements IReader {
	/** related selector */
	private final ISelector selector;
	/**
	 * constructor
	 * @param selector
	 */
	public Reader(ISelector selector) {
		this.selector = selector;
	}
	/**
	 * ref the related selector.
	 * @return
	 */
	protected ISelector getSelector() {
		return selector;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		IContainer container = (IContainer)selector.select(channel);
		if(container != null) {
			container.load(channel);
		}
		return container;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IContainer> getRemainData() throws Exception {
		return null;
	}
}
