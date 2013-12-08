package com.ttProject.container.flv.type;

import com.ttProject.container.flv.FlvTag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit8;

/**
 * メッセージ集合のtag
 * 通常のflvでは、存在しないタグっぽいです。(rtmpの変換用？)
 * @author taktod
 *
 */
public class AggregateTag extends FlvTag {
	public AggregateTag(Bit8 tagType) {
		super(tagType);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {

	}
	@Override
	public void load(IReadChannel channel) throws Exception {

	}
}
