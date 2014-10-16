/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.riff.type.Movi;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * RiffMasterUnit
 * base for riff unit with child units.
 * @author taktod
 */
public abstract class RiffMasterUnit extends RiffSizeUnit {
	private List<RiffUnit> childUnits = new ArrayList<RiffUnit>();
	private IReader reader = null;
	/**
	 * constructor
	 * @param type
	 */
	public RiffMasterUnit(Type type) {
		super(type);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// try to read the riffUnit.
		int targetSize = getSize() - 8;
		IContainer container = null;
		while(targetSize > 4 && (container = reader.read(channel)) != null) {
			targetSize -= container.getSize();
			if(container instanceof Movi) {
				// in the case of Movi is found. stop and leave from MasterUnit and back to original work.(let the original process to handle frame unit.)
				return;
			}
			if(container instanceof RiffUnit) {
				childUnits.add((RiffUnit)container);
				// if get the data(chunk for data. need to fall back to original test and try to analyze frame.)
			}
		}
		if(targetSize > 0) {
			BufferUtil.quickDispose(channel, targetSize);
			targetSize = 0;
		}
	}
	/**
	 * set the unit reader.
	 * @param reader
	 */
	public void setRiffUnitReader(IReader reader) {
		this.reader = reader;
	}
}
