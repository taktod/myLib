package com.ttProject.container.flv.type;

import com.ttProject.container.flv.FlvTag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN.Bit24;

/**
 * 映像用のtag
 * @author taktod
 */
public class VideoTag extends FlvTag {
	private Bit4 frameType;
	private Bit4 codecId;
	private Bit8 packetType; // avcのみ
	private Bit24 dts; // avcのみ
	private IUnit frame; // 動作対象フレーム
	public VideoTag(Bit8 tagType) {
		super(tagType);
	}
	public VideoTag() {
		this(new Bit8(0x09));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		// コーデック情報等を取得する必要あり
		BitLoader loader = new BitLoader(channel);
		loader.load();
	}
}
