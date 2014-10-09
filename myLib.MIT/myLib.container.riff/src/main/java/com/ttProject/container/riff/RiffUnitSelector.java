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
import com.ttProject.container.riff.type.List;
import com.ttProject.container.riff.type.Riff;
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
	private Logger logger = Logger.getLogger(RiffUnitSelector.class);
	/** format information */
	private RiffFormatUnit formatUnit = null;
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
		logger.info(type);
		switch(type) {
		case RIFF: // header
			if(channel.position() != 4) {
				throw new Exception("position of header is invalid.");
			}
			unit = new Riff();
//			unit = new RiffHeaderUnit();
//			headerUnit = (RiffHeaderUnit)unit;
			break;
		case FMT: // format information(must)
			unit = new Fmt();
			formatUnit = (Fmt)unit;
			break;
		case FACT: // sampleNum and so on...
			unit = new Fact();
			break;
		case DATA: // data body.(must)
			unit = new Data();
			break;
		case LIST: // ?
			unit = new List();
			break;
		case hdrl:
			break;
		default:
			throw new RuntimeException("unexpected frame type.:" + type);
		}
		if(unit == null) {
			throw new Exception("unit is undefined.maybe non-support type.:" + type);
		}
		if(!(unit instanceof RiffMasterUnit) || !(unit instanceof RiffFormatUnit)) {
			unit.setFormatUnit(formatUnit);
		}
		unit.minimumLoad(channel);
		return unit;
	}
}
