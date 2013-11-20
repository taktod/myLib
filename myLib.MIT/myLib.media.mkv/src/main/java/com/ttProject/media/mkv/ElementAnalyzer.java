package com.ttProject.media.mkv;

import com.ttProject.nio.channels.IReadChannel;

/**
 * エレメントデータを解析します。
 * @author taktod
 */
public class ElementAnalyzer implements IElementAnalyzer {
	private final MkvManager manager = new MkvManager();
	@Override
	public Element analyze(IReadChannel ch) throws Exception {
		Element element = manager.getUnit(ch);
		if(element == null) {
			return null;
		}
		return element;
	}
}
