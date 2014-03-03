package com.ttProject.container.mp4.table;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * Dinfの中にある、url というtagのボックス
 * @author taktod
 */
public class DataEntryUrlBox extends Mp4Atom {
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24(); // 1がはいるっぽい
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public DataEntryUrlBox(Bit32 size, Bit32 name) {
		super(size, name);
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
	protected void requestUpdate() throws Exception {
		
	}
}
