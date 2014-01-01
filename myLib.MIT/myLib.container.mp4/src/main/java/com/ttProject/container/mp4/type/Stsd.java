package com.ttProject.container.mp4.type;

import java.util.List;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * stsdの定義
 * @author taktod
 */
public class Stsd extends Mp4Atom {
	private Bit8 version;
	private Bit24 flags;
	private Bit32 count;
//	private List<DescriptionRecord> descriptions; 
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Stsd(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Stsd() {
		super(new Bit32(), Type.getTypeBit(Type.Stsd));
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// stsdについて読み込みをつづける。
		// ここからstsdを読み込ませる必要がある。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
