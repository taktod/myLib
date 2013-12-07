package com.ttProject.container.flv;

import java.nio.ByteBuffer;

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
public abstract class FlvTag implements IContainer {
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
	}
	/**
	 * {@inheritDoc}
	 * こちらはファイル上に展開させた場合の位置となります。
	 * rtmpで転送されてきたデータからつくった場合とかは重要にはなりません。
	 */
	@Override
	public int getPosition() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 * flvは全体サイズとは別に２つのサイズがあるので注意
	 */
	@Override
	public long getSize() {
		if(dataSize == null) {
			return -1;
		}
		return dataSize.get() + 11 + 4;
	}
	/**
	 * {@inheritDoc}
	 * tagの全体のデータを応答します
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPts() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 * flvは1000固定です。
	 */
	@Override
	public long getTimebase() {
		return 1000L;
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
