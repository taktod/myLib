/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container;

import com.ttProject.nio.channels.IReadChannel;

/**
 * null container for dummy.
 * @author taktod
 */
public class NullContainer extends Container {
	/** instance of dummy. share this. */
	private static final NullContainer instance = new NullContainer();
	/**
	 * ref the shared memory instance.
	 * @return
	 */
	public static NullContainer getInstance() {
		return instance;
	}
	/**
	 * constructor, prohibited by private.
	 */
	private NullContainer() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		throw new RuntimeException("call minimumLoad for nullContainer.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		throw new RuntimeException("call load for nullContainer.");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		throw new RuntimeException("call requestUpdate for nullContainer.");
	}
}
