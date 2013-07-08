package com.ttProject.media.flv;

import com.ttProject.nio.channels.IReadChannel;

public class TagAnalyzer implements ITagAnalyzer {
	@Override
	public Tag analyze(IReadChannel ch) throws Exception {
		Tag tag = Tag.getTag(ch);
		if(tag == null) {
			return null;
		}
		// tagデータの実データ部のみ、読み込みさせる。(Tag.getTagを実行すると、fileのpointerが先頭部分だけすすんでいるため。)
		tag.analyze(ch, false);
		ch.position(tag.getPosition() + tag.getSize());
		return tag;
	}
}
