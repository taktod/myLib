/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * avih
 * @see http://msdn.microsoft.com/ja-jp/library/cc352261.aspx
 * @author taktod
 */
public class Avih extends RiffSizeUnit {
	private Bit32 dwMicroSecPerFrame    = new Bit32();
	private Bit32 dwMaxBytesPerSec      = new Bit32();
	private Bit32 dwPaddingGranularity  = new Bit32();
	private Bit32 dwFlags               = new Bit32();
	private Bit32 dwTotalFrames         = new Bit32();
	private Bit32 dwInitialFrames       = new Bit32();
	private Bit32 dwStreams             = new Bit32();
	private Bit32 dwSuggestedBufferSize = new Bit32();
	private Bit32 dwWidth               = new Bit32();
	private Bit32 dwHeigth              = new Bit32();
	private Bit32[] dwReserved          = new Bit32[4];
	{
		for(int i = 0;i < 4;i ++) {
			dwReserved[i] = new Bit32();
		}
	}
	/**
	 * constructor
	 */
	public Avih() {
		super(Type.avih);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(dwMicroSecPerFrame, dwMaxBytesPerSec, dwPaddingGranularity, dwFlags,
				dwTotalFrames, dwInitialFrames, dwStreams, dwSuggestedBufferSize,
				dwWidth, dwHeigth);
		loader.load(dwReserved);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
