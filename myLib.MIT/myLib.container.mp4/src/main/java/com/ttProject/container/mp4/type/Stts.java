package com.ttProject.container.mp4.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * sttsの定義
 * @author taktod
 */
public class Stts extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Stts.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Bit32 count = null;
	/** 内容データ保持 */
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Stts(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Stts() {
		super(new Bit32(), Type.getTypeBit(Type.Stts));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(version, flags);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// ここで読み込みを実行する。
		count = new Bit32();
		BitLoader loader = new BitLoader(channel);
		loader.load(count);
		// 内容データを一期に読み込んでおく。
		buffer = BufferUtil.safeRead(channel, count.get() * 8);
		super.load(channel);
	}
	/**
	 * 内部のデータを初めの位置に戻す
	 */
	public void reset() {
		
	}
	/**
	 * 次のデータの長さを問い合わせる
	 * @return
	 */
	public int nextDuration() {
		return -1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
