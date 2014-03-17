package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.container.mp4.table.SampleFlags;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * trunの定義
 * @author taktod
 */
public class Trun extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Trun.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Bit32 sampleCount = null;
	private Bit32 dataOffset = null;
	private SampleFlags firstSampleFlags = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Trun(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Trun() {
		super(new Bit32(), Type.getTypeBit(Type.Trun));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
