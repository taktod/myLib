/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv;

import org.apache.log4j.Logger;

import com.ttProject.container.Container;
import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * flv header tag.
 * first 13 bytes for flv file.
 * @author taktod
 */
public class FlvHeaderTag extends Container implements IContainer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FlvHeaderTag.class);

	private final Bit24 signature;
	private       Bit8  version   = new Bit8(1);
	private       Bit5  reserved1 = new Bit5();
	private       Bit1  audioFlag = new Bit1();
	private       Bit1  reserved2 = new Bit1();
	private       Bit1  videoFlag = new Bit1();
	private       Bit32 length    = new Bit32(9);
	private       Bit32 reserved3 = new Bit32();
	/**
	 * constructor
	 * @param signature
	 */
	public FlvHeaderTag(Bit24 signature) {
		this.signature = signature;
		setPosition(0);
		setPts(0);
		setTimebase(1000);
		super.update();
	}
	/**
	 * constructor
	 */
	public FlvHeaderTag() {
		this(new Bit24('F' << 16 | 'L' << 8 | 'V'));
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// read all data here.
		BitLoader loader = new BitLoader(channel);
		loader.load(version, reserved1, audioFlag, reserved2, videoFlag,
				length, reserved3);
		super.update();
		setSize(13);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.update();
	}
	/**
	 * set video track.
	 * @param flag
	 */
	public void setVideoFlag(boolean flag) {
		videoFlag.set(flag ? 1 : 0);
		super.update();
	}
	/**
	 * set audio track.
	 * @param flag
	 */
	public void setAudioFlag(boolean flag) {
		audioFlag.set(flag ? 1 : 0);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		setData(connector.connect(signature, version, reserved1,
				audioFlag, reserved2, videoFlag, length, reserved3));
	}
}
