/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode;

import java.util.List;

import com.ttProject.media.Unit;

/**
 * 各トラックごとの変換結果を受け取るリスナー
 * @author taktod
 */
public interface ITrackListener {
	/**
	 * 変換処理後のunitデータを受けとります。
	 * @param unit
	 */
	public void receiveData(List<Unit> units);
	/**
	 * 終了処理
	 */
	public void close();
}
