/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.frame.IFrame;

/**
 * フレームを取り出したときのレポート動作
 * @author taktod
 */
public interface IFrameEventListener {
	/**
	 * dataの解析動作中にframeを見つけたら実施されるイベント
	 * @param frame
	 */
	public void onNewFrame(IFrame frame);
}
