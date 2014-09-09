/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.chunk.aac.analyzer;

import com.ttProject.chunk.aac.AacDataList;
import com.ttProject.media.Unit;
import com.ttProject.media.aac.frame.Aac;

/**
 * aacのframeをaacDataListに格納していきます。
 * @author taktod
 */
public class AacFrameAnalyzer implements IAacFrameAnalyzer {
	/** aacのデータ保持オブジェクト */
	private AacDataList aacDataList;
	/**
	 * aacDataListを設定します
	 */
	@Override
	public void setAacDataList(AacDataList aacDataList) {
		this.aacDataList = aacDataList;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(Unit unit) {
		if(unit instanceof Aac) {
			Aac aac = (Aac) unit;
			aacDataList.addAacData(aac);
		}
	}
}
