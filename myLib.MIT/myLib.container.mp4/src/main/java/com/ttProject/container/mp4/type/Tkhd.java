package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * tkhdの定義
 * @author taktod
 */
@SuppressWarnings("unused")
public class Tkhd extends Mp4Atom {
	private Bit8 version;
	private Bit24 flags;
	private Bit32 creationTime = null; // version0の場合
	private Bit64 creationTime1 = null; // version1の場合
	private Bit32 modificationTime = null; // version0の場合
	private Bit64 modificationTime1 = null; // version1の場合
	private Bit32 trackId;
	private Bit32 reserved1;
	private Bit32 duration = null;
	private Bit64 duration1 = null;
	private Bit32[] reserved2 = new Bit32[2];
	private Bit16 layer;
	private Bit16 alternateGroup;
	private Bit16 volume;
	private Bit16 reserved3;
	private Bit32[] transformMatrix = new Bit32[9];
	private Bit32 width;
	private Bit32 height;
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
	protected void requestUpdate() throws Exception {
	}
}
