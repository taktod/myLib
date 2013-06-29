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
	@Override
	public void close() {
	}
	@Override
	public boolean requiresLayout() {
		return false;
	}
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
