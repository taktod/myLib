/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;

/**
 * NDCの情報をデータ挿入時に合わせるためのappender
 * ログの書き込み処理というより、データ書き込み時Hookとして利用しています。
 * できたら、ロガーの順位として上位にいれておくと吉です。
 * @author taktod
 */
public class NDCInjectAppender extends AppenderSkeleton {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void append(LoggingEvent event) {
		// 書き込み時に登録したいデータとNDCに登録されているデータの数を比べてずれている場合は、初期化しなおす。
		if(NDC.getDepth() != AllThreadNDCInjection.data.size()) {
			NDC.clear();
			for(String item : AllThreadNDCInjection.data) {
				NDC.push(item);
			}
		}
	}
}
