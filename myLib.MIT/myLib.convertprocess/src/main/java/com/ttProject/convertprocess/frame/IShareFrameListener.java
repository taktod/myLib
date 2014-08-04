/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.convertprocess.frame;

import com.ttProject.frame.IFrame;

/**
 * 共有フレームデータをうけとったときの動作
 * @author taktod
 */
public interface IShareFrameListener {
	/**
	 * フレーム処理を実施します。
	 * @param frame
	 * @param id
	 */
	public void pushFrame(IFrame frame, int id);
}
