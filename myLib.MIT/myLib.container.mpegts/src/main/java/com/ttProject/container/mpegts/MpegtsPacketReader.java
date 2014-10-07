/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.NullContainer;
import com.ttProject.container.Reader;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * mpegtsPacketReader
 * @author taktod
 */
public class MpegtsPacketReader extends Reader {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MpegtsPacketReader.class);
	/** hold the unitStart Pes object for reference. */
	private Map<Integer, Pes> pesMap = new ConcurrentHashMap<Integer, Pes>();
	/**
	 * constructor
	 */
	public MpegtsPacketReader() {
		super(new MpegtsPacketSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		IUnit unit = getSelector().select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		/*
		 * if pes is not completed yet. reply nullContainer.
		 */
		if(unit instanceof Pes) {
			Pes pes = (Pes) unit;
			if(pes.isPayloadUnitStart()) {
				Pes prevPes = pesMap.get(pes.getPid());
				pesMap.put(pes.getPid(), pes);
				if(prevPes != null) {
					prevPes.analyzeFrame();
					return prevPes;
				}
			}
			return NullContainer.getInstance();
		}
		return (IContainer) unit;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IContainer> getRemainData() throws Exception {
		List<IContainer> result = new ArrayList<IContainer>();
		for(Entry<Integer, Pes> entry : pesMap.entrySet()) {
			Pes pes = entry.getValue();
			if(pes != null) {
				pes.analyzeFrame();
				result.add(pes);
			}
		}
		return result;
	}
}
