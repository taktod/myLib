package com.ttProject.container.flv;

import com.ttProject.container.Container;
import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN.Bit24;
import com.ttProject.unit.extra.BitN.Bit32;

/**
 * flvデータのタグ
 * @author taktod
 */
public abstract class FlvTag extends Container implements IContainer {
	private Bit8 tagType; // 8 9 12以外にもありえるのか？
	private Bit24 dataSize;
	private Bit24 timestamp;
	private Bit8 timestampExt;
	private Bit24 streamId;
	private Bit32 prevTagSize;
	/**
	 * コンストラクタ
	 */
	public FlvTag(Bit8 tagType) {
		this.tagType = tagType;
		setTimebase(1000); // flvはtimebaseがかならず1000になります。
	}
	/**
	 * 
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// 先頭の11バイト読み込みます。
		// コンストラクタを読み込んだときに、すでに1バイト読み込み済みなので、残りの10バイトとりあえず読んでおきたい。
		BitLoader loader = new BitLoader(channel);
		dataSize = new Bit24();
		timestamp = new Bit24();
		timestampExt = new Bit8();
		streamId = new Bit24();
		loader.load(dataSize, timestamp, timestampExt, streamId);
		prevTagSize = new Bit32(dataSize.get() + 11);
	}
}
