/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * RiffのheaderUnit
 * @author taktod
 */
public class RiffHeaderUnit extends RiffUnit {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(RiffHeaderUnit.class);
	/** フォーマット情報WAVEとか */
	private String formatString;
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// タグタイプも読み込みます。
		formatString = new String(BufferUtil.safeRead(channel, 4).array());
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// 特にすることなし
	}
	@Override
	protected void requestUpdate() throws Exception {

	}
	public String getFormatString() {
		return formatString;
	}
}
