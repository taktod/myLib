/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.type.Data;
import com.ttProject.container.riff.type.Fact;
import com.ttProject.container.riff.type.Fmt;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

/**
 * riff unit selector
 * @author taktod
 */
public class RiffUnitSelector implements ISelector {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(RiffUnitSelector.class);
	/** headerUnit */
	private RiffHeaderUnit headerUnit = null;
	/** format information */
	private Fmt fmt = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		// check first 4byte
		Type type = Type.getType(BufferUtil.safeRead(channel, 4).getInt());
		RiffUnit unit = null;
		switch(type) {
		case RIFF: // header
			if(channel.position() != 4) {
				throw new Exception("position of header is invalid.");
			}
			unit = new RiffHeaderUnit();
			headerUnit = (RiffHeaderUnit)unit;
			break;
		case FMT: // format information(must)
			unit = new Fmt();
			fmt = (Fmt)unit;
			break;
		case FACT: // sampleNum and so on...
			unit = new Fact();
			break;
		case DATA: // data body.(must)
			unit = new Data();
			break;
		case LIST: // ?
			break;
		default:
			throw new RuntimeException("unexpected frame type.:" + type);
		}
		if(unit == null) {
			throw new Exception("unit is undefined.maybe non-support type.:" + type);
		}
		if(!(unit instanceof RiffHeaderUnit)) {
			unit.setHeaderUnit(headerUnit);
			if(!(unit instanceof Fmt)) {
				unit.setFmt(fmt);
			}
		}
		unit.minimumLoad(channel);
		return unit;
	}
}
