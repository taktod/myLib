package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * mvhdの定義
 * @author taktod
 */
public class Mvhd extends Mp4Atom {
	private Bit8    version;
	private Bit24   flags;
	private Bit32   creationTime = null; // version0の場合
	private Bit64   creationTime1 = null; // version1の場合
	private Bit32   modificationTime = null; // version0の場合
	private Bit64   modificationTime1 = null; // version1の場合
	private Bit32   timeScale;
	private Bit32   duration = null;
	private Bit64   duration1 = null;
	private Bit32   playbackRate;
	private Bit16   volume;
	private Bit16   reserved1;
	private Bit32[] reserved2 = new Bit32[2];
	private Bit32[] matrix = new Bit32[9];
	private Bit32[] reserved = new Bit32[6];
	private Bit32   nextTrackId; // 次追加する場合のtrackIdか？
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Mvhd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Mvhd() {
		super(new Bit32(), Type.getTypeBit(Type.Mvhd));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
