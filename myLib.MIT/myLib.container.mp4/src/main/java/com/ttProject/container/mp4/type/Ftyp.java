package com.ttProject.container.mp4.type;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * ftypの定義
 * @author taktod
 */
public class Ftyp extends Mp4Atom {
	private Bit32 majorBrand = new Bit32();
	private Bit32 minorVersion = new Bit32();
	private List<Bit32> compatibleBrands = new ArrayList<Bit32>();
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Ftyp(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Ftyp() {
		super(new Bit32(), Type.getTypeBit(Type.Ftyp));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
