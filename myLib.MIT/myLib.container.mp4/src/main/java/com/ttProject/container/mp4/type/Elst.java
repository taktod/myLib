package com.ttProject.container.mp4.type;

import java.util.List;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.container.mp4.table.EditListEntryTable;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * elstの定義
 * @author taktod
 * これは必須ではないみたいなので、スルーしておきます。
 */
@SuppressWarnings("unused")
public class Elst extends Mp4Atom {
	private Bit8 version;
	private Bit24 flags;
	private Bit32 entryCount;
	private List<EditListEntryTable> tableList = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Elst(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Elst() {
		super(new Bit32(), Type.getTypeBit(Type.Elst));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
