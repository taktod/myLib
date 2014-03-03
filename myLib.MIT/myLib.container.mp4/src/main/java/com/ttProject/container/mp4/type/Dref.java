package com.ttProject.container.mp4.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.container.mp4.table.DataEntryUrlBox;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.IntUtil;

/**
 * drefの定義
 * @author taktod
 */
public class Dref extends Mp4Atom {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Dref.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Bit32 entryCount = null;
	private DataEntryUrlBox[] entries;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Dref(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Dref() {
		super(new Bit32(), Type.getTypeBit(Type.Dref));
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
		entryCount = new Bit32();
		BitLoader loader = new BitLoader(channel);
		loader.load(entryCount);
		entries = new DataEntryUrlBox[entryCount.get()];
		for(int i = 0;i < entryCount.get();i ++) {
			// sizeと名前を読み込む
			Bit32 size = new Bit32();
			Bit32 name = new Bit32();
			loader.load(size, name);
			String nameString = IntUtil.makeHexString(name.get());
			if("url ".equals(nameString)) {
				entries[i] = new DataEntryUrlBox(size, name);
				entries[i].minimumLoad(channel);
				entries[i].load(channel);
			}
			else {
				throw new Exception("想定外のDataEntryBoxを検出しました。:" + nameString);
			}
		}
		super.load(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
