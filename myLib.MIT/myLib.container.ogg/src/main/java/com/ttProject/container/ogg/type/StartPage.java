package com.ttProject.container.ogg.type;

import org.apache.log4j.Logger;

import com.ttProject.container.ogg.OggPage;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * startPage(speexとかのheader情報がはいっているっぽい。)
 * @author taktod
 */
public class StartPage extends OggPage {
	/** ロガー */
	private Logger logger = Logger.getLogger(StartPage.class);
	/**
	 * コンストラクタ
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public StartPage(Bit8 version, Bit5 zeroFill,
			Bit1 logicEndFlag, Bit1 logicStartFlag,
			Bit1 packetContinurousFlag) {
		super(version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		logger.info("minimumload");
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		logger.info("load on startPage");
		channel.position(getPosition() + 27);
		// 次の位置に強制割り当てしている
		channel.position(getPosition() + getSize());
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
