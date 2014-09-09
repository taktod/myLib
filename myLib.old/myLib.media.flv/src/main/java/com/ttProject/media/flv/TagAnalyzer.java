/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.flv;

import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.IReadChannel;

public class TagAnalyzer implements ITagAnalyzer {
	private final FlvManager manager = new FlvManager();
	@Override
	public Tag analyze(IReadChannel ch) throws Exception {
		Tag tag = null;
		do {
			if(tag != null) {
				ch.position(tag.getPosition() + tag.getInitSize());
			}
			tag = manager.getUnit(ch);
			if(tag == null) {
				return null;
			}
		} while((tag instanceof VideoTag || tag instanceof AudioTag) && tag.getSize() <= 15); // メディアデータなのに、内容がない場合は合っても仕方ないので捨てます。
		// tagデータの実データ部のみ、読み込みさせる。(Tag.getTagを実行すると、fileのpointerが先頭部分だけすすんでいるため。)
		tag.analyze(ch, false);
		ch.position(tag.getPosition() + tag.getInitSize());
		return tag;
	}
}
