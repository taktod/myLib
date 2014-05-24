/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp4;

import org.apache.log4j.Logger;

import com.ttProject.media.mp4.atom.Stsd;
import com.ttProject.media.mp4.atom.stsd.RecordAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class AtomAnalyzer implements IAtomAnalyzer {
	private Logger logger = Logger.getLogger(AtomAnalyzer.class);
	private final Mp4Manager manager = new Mp4Manager();
	@Override
	public Atom analyze(IReadChannel ch) throws Exception {
		Atom atom = manager.getUnit(ch);
		if(atom == null) {
			return null;
		}
		if(atom instanceof Stsd) {
			try {
				atom.analyze(ch, new RecordAnalyzer());
			}
			catch (Exception e) {
				logger.error("flvに適合しないデータを発見しました。", e);
			}
		}
		else {
			atom.analyze(ch, this);
		}
		ch.position(atom.getPosition() + atom.getSize());
		return atom;
	}
}
