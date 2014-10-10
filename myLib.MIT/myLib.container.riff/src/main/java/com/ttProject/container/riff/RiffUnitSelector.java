/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.type.Avih;
import com.ttProject.container.riff.type.Data;
import com.ttProject.container.riff.type.Fact;
import com.ttProject.container.riff.type.Fmt;
import com.ttProject.container.riff.type.Hdrl;
import com.ttProject.container.riff.type.Junk;
import com.ttProject.container.riff.type.List;
import com.ttProject.container.riff.type.Riff;
import com.ttProject.container.riff.type.Strf;
import com.ttProject.container.riff.type.Strh;
import com.ttProject.container.riff.type.Strl;
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
	private Strh prevStrhUnit = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		// check first 4byte
		logger.info(Integer.toHexString(channel.position()));
		Type type = Type.getType(BufferUtil.safeRead(channel, 4).getInt());
		RiffUnit unit = null;
		logger.info(type);
		switch(type) {
		case RIFF: // header
			if(channel.position() != 4) {
				throw new Exception("position of header is invalid.");
			}
			unit = new Riff();
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
			unit = new Hdrl();
			break;
		case avih:
			unit = new Avih();
			break;
		case strl:
			unit = new Strl();
			break;
		case strh:
			unit = new Strh();
			prevStrhUnit = (Strh)unit;
			break;
		case strf:
			// check strh, if
			switch(prevStrhUnit.getFccType()) {
			case auds:
				// use fmt
				unit = new Fmt();
				break;
			case mids:
			case tets:
				throw new Exception("unknown for mids or tets.");
			case vids:
				logger.info("strf is settled");
				unit = new Strf();
				break;
			}
			formatUnit = (RiffFormatUnit) unit;
			break;
		case JUNK:
			unit = new Junk();
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
