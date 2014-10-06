/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Info
 * @author taktod
 */
public class Info extends MkvMasterTag {
	/**
	 * constructor
	 * @param size
	 */
	public Info(EbmlValue size) {
		super(Type.Info, size);
	}
	/**
	 * constructor
	 */
	public Info() {
		this(new EbmlValue());
	}
	/**
	 * constructor
	 * @param position
	 */
	public Info(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * set position
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
	/**
	 * setup information.
	 * @param timecodeScale 1000000L(ナノ秒単位で指定、この値だと1ミリ秒刻みになります)
	 * @param muxApp
	 * @param writeApp
	 * @return timecodeScaleの実動作値を応答します。通常なら1000(１ミリ秒刻みなため)
	 */
	public long setup(long scale, String muxApp, String writeApp) throws Exception {
		TimecodeScale timecodeScale = new TimecodeScale();
		timecodeScale.setValue(scale);
		addChild(timecodeScale);
		
		MuxingApp muxingApp = new MuxingApp();
		muxingApp.setValue(muxApp);
		addChild(muxingApp);
		
		WritingApp writingApp = new WritingApp();
		writingApp.setValue(writeApp);
		addChild(writingApp);
		
		// TODO in prepareTailer for writer. here is the position to pt duration(doubleTag)
		Void voidTag = new Void();
		voidTag.setTagSize(9);
		addChild(voidTag);
		
		return timecodeScale.getTimebaseValue();
	}
}
