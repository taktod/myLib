package com.ttProject.container.flv;

import java.nio.ByteBuffer;

import com.ttProject.container.Container;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * flvデータのタグ
 * @author taktod
 * メモ：bitDataの取り扱いについて
 * コンストラクタにて設定されるものはfinalをつける。
 * minimumloadにて設定されるものは先にデフォルトのbitで初期化する
 * データのフラグによって追加されるかもしれないものはnullを代入しておく
 */
public abstract class FlvTag extends Container {
	private final Bit8  tagType; // 8 9 12以外にもありえるのか？
	private       Bit24 dataSize     = new Bit24();
	private       Bit24 timestamp    = new Bit24();
	private       Bit8  timestampExt = new Bit8();
	private       Bit24 streamId     = new Bit24();
	private       Bit32 prevTagSize  = new Bit32();
	/**
	 * コンストラクタ
	 */
	public FlvTag(Bit8 tagType) {
		this.tagType = tagType;
		super.setTimebase(1000); // flvはtimebaseがかならず1000になります。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// 先頭の11バイト読み込みます。
		// コンストラクタを読み込んだときに、すでに1バイト読み込み済みなので、残りの10バイトとりあえず読んでおきたい。
		// 1つ前の位置を保持しておく。
		super.setPosition(channel.position() - 1);
		// データの読み込みを進める
		BitLoader loader = new BitLoader(channel);
		loader.load(dataSize, timestamp, timestampExt, streamId);
		prevTagSize = new Bit32(dataSize.get() + 11);
		super.setPts(timestampExt.get() << 24 | timestamp.get());
		super.setSize(dataSize.get() + 11 + 4);
		super.update();
	}
	/**
	 * 開始時に存在するデータ参照
	 * @return
	 */
	protected ByteBuffer getStartBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(tagType, dataSize, timestamp, timestampExt, streamId);
	}
	/**
	 * 終端に追加するデータ参照
	 * @return
	 */
	protected ByteBuffer getTailBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(prevTagSize);
	}
	/**
	 * 終端に追加されるデータ量情報(全体のデータ量 - 4)
	 * @return
	 */
	protected int getPrevTagSize() {
		return prevTagSize.get();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData(ByteBuffer data) {
		dataSize.set(data.remaining() - 11 - 4);
		prevTagSize = new Bit32(dataSize.get() + 11);
		super.setData(data);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPts(long pts) {
		timestamp.set((int)(pts & 0x00FFFFFF));
		timestampExt.set((int)(pts >>> 24) & 0xFF);
		super.setPts(pts);
	}
	protected void setSize(int size) {
		dataSize.set(size - 15);
		prevTagSize.set(size - 4);
		super.setSize(size);
	}
}
