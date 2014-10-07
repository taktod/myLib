/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.container.Container;
import com.ttProject.container.riff.type.Fmt;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * riff unit
 * @author taktod
 */
public abstract class RiffUnit extends Container {
	/** headerUnit object */
	private RiffHeaderUnit headerUnit = null;
	/** format information */
	private Fmt fmt = null;
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		Bit32 size = new Bit32();
		loader.load(size);
		super.setSize(size.get());
	}
	protected void setHeaderUnit(RiffHeaderUnit headerUnit) {
		this.headerUnit = headerUnit;
	}
	protected void setFmt(Fmt fmt) {
		this.fmt = fmt;
	}
	public RiffHeaderUnit getHeaderUnit() {
		return headerUnit;
	}
	public Fmt getFmt() {
		return fmt;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(getClass().getSimpleName());
		data.append(" size:").append(getSize());
		return data.toString();
	}
}
