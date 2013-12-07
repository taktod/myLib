package com.ttProject.container.flv.type;

import com.ttProject.container.flv.FlvTag;
import com.ttProject.nio.channels.IReadChannel;

/**
 * metaデータ
 * TODO このタグは、前のプログラムでは動作が微妙だったので・・・(sizeがふらふらかわって使いにくい)
 * 今回はもっとしっかり動作するようにしたいところ。
 * たしかデータを追加すると、サイズがかわってしまって、タイミングによっては正しいデータがとれないとかあったはず。
 * @author taktod
 */
public class MetaTag extends FlvTag {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		
	}
}
