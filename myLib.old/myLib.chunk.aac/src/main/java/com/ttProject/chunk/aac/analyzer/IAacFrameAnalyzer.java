/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.aac.analyzer;

import com.ttProject.chunk.aac.AacDataList;
import com.ttProject.media.Unit;

/**
 * unitの中身を解析して必要なaacFrameを応答する動作
 * @author taktod
 */
public interface IAacFrameAnalyzer {
	/**
	 * aacDataListをセットします。
	 * @param aacDataList
	 */
	public void setAacDataList(AacDataList aacDataList);
	/**
	 * 解析を実行します
	 * @param unit
	 */
	public void analyze(Unit unit);
}
