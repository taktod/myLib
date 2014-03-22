package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.container.mp4.table.SampleFlags;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * trexの定義
 * @author taktod
 * fragmentedMp4の場合でdurationが一定の場合は、ここのdefaultSampleDurationを使いまわしていくことがあるらしい。
 */
public class Trex extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Trex.class);
	private Bit8 version = new Bit8();
	private Bit24 flags = new Bit24();
	private Bit32 trackId = null;
	private Bit32 defaultSampleDescriptionIndex = null;
	private Bit32 defaultSampleDuration = null;
	private Bit32 defaultSampleSize = null;
	private SampleFlags defaultSampleFlags = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Trex(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Trex() {
		super(new Bit32(), Type.getTypeBit(Type.Trex));
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
		trackId = new Bit32();
		defaultSampleDescriptionIndex = new Bit32();
		defaultSampleDuration = new Bit32();
		defaultSampleSize = new Bit32();
		defaultSampleFlags = new SampleFlags();
		BitLoader loader = new BitLoader(channel);
		loader.load(trackId, defaultSampleDescriptionIndex, defaultSampleDuration, defaultSampleSize, defaultSampleFlags);
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
