package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * tkhdの定義
 * @author taktod
 */
public class Tkhd extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Tkhd.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Bit     creationTime     = null;
	private Bit     modificationTime = null;
	private Bit32   trackId          = null;
	private Bit32   reserved1        = null;
	private Bit     duration         = null;
	private Bit32[] reserved2        = new Bit32[2];
	private Bit16   layer            = null;
	private Bit16   alternateGroup   = null;
	private Bit16   volume           = null;
	private Bit16   reserved3        = null;
	private Bit32[] transformMatrix  = new Bit32[9];
	private Bit32   width            = null; // 16.16
	private Bit32   height           = null; // 16.16になっているので、注意
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Tkhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Tkhd() {
		super(new Bit32(), Type.getTypeBit(Type.Tkhd));
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
	/**		transformMatrix[0] = new Bit32();

	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(version.get() == 0) {
			creationTime = new Bit32();
			modificationTime = new Bit32();
			duration = new Bit32();
		}
		else if(version.get() == 1) {
			creationTime = new Bit64();
			modificationTime = new Bit64();
			duration = new Bit64();
		}
		else {
			throw new Exception("versionの値が不正です。");
		}
		trackId = new Bit32();
		reserved1 = new Bit32();
		reserved2[0] = new Bit32();
		reserved2[1] = new Bit32();
		layer = new Bit16();
		alternateGroup = new Bit16();
		volume = new Bit16();
		reserved3 = new Bit16();
		transformMatrix[0] = new Bit32();
		transformMatrix[1] = new Bit32();
		transformMatrix[2] = new Bit32();
		transformMatrix[3] = new Bit32();
		transformMatrix[4] = new Bit32();
		transformMatrix[5] = new Bit32();
		transformMatrix[6] = new Bit32();
		transformMatrix[7] = new Bit32();
		transformMatrix[8] = new Bit32();
		width = new Bit32();
		height = new Bit32();
		BitLoader loader = new BitLoader(channel);
		loader.load(creationTime, modificationTime, trackId, reserved1, duration);
		loader.load(reserved2);
		loader.load(layer, alternateGroup, volume, reserved3);
		loader.load(transformMatrix);
		loader.load(width, height);
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
