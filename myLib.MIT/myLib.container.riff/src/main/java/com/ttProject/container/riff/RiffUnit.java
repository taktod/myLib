/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.container.Container;

/**
 * riff unit
 * @author taktod
 */
public abstract class RiffUnit extends Container {
	private final Type fcc; // fourcc
	/** format information */
	private RiffFormatUnit formatUnit = null;
	/**
	 * constructor
	 * @param type
	 */
	public RiffUnit(Type type) {
		fcc = type;
		super.setSize(4);
	}
	/**
	 * ref the fourCC
	 * @return
	 */
	public Type getFourCC() {
		return fcc;
	}
	protected void setFormatUnit(RiffFormatUnit formatUnit) {
		this.formatUnit = formatUnit;
	}
	protected RiffFormatUnit getFormatUnit() {
		return formatUnit;
	}
/*	@Override
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
	// TODO this riff unit is for wave only?
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
	}*/
}
