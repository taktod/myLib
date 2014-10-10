/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.RiffMasterUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;

/**
 * list
 * I need sample. what's this?
 * 
 * @see http://msdn.microsoft.com/ja-jp/library/cc352264.aspx
 * 
 * @author taktod
 */
public class List extends RiffMasterUnit {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(List.class);
	/**
	 * constructor
	 */
	public List() {
		super(Type.LIST);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// LIST (hdrl)
		super.minimumLoad(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
