package com.ttProject.container.mpegts;

import com.ttProject.container.IContainer;
import com.ttProject.container.Reader;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * mpegtsPacketを解析します。
 * @author taktod
 */
public class MpegtsPacketReader extends Reader {
	/**
	 * コンストラクタ
	 */
	public MpegtsPacketReader() {
		super(new MpegtsPacketSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		IUnit unit = getSelector().select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		// TODO ここに細工を実施します。
		/*
		 * 未完了pesの場合はNullUnitを応答します。
		 */
		return (IContainer) unit;
	}
}
