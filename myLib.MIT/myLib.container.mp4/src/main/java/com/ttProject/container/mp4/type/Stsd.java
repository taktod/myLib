package com.ttProject.container.mp4.type;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.container.mp4.stsd.DescriptionRecord;
import com.ttProject.container.mp4.stsd.StsdAtomReader;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * stsdの定義
 * @author taktod
 */
public class Stsd extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Stsd.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Bit32 count   = new Bit32();
	private List<DescriptionRecord> descriptions = new ArrayList<DescriptionRecord>(); 
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
		StsdAtomReader stsdAtomReader = new StsdAtomReader();
		BitLoader loader = new BitLoader(channel);
		loader.load(version, flags, count);
		for(int i = 0;i < count.get();i ++) {
			DescriptionRecord record = (DescriptionRecord)stsdAtomReader.read(channel);
			if(record == null) {
				logger.info("recordがありませんでした。");
			}
			else {
				logger.info(record);
				descriptions.add(record);
			}
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
