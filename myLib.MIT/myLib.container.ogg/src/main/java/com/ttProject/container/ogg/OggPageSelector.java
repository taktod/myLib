package com.ttProject.container.ogg;

import org.apache.log4j.Logger;

import com.ttProject.container.ogg.type.Page;
import com.ttProject.container.ogg.type.StartPage;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * oggのpageについて調べる
 * @author taktod
 */
public class OggPageSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggPageSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() == channel.position()) {
			// もうデータがない
			return null;
		}
		logger.info("位置:" + Integer.toHexString(channel.position()));
		// 先頭データ4byteを取得して、OggSであるか確認
		String pattern = new String(BufferUtil.safeRead(channel, 4).array());
		if(!OggPage.capturePattern.equals(pattern)) {
			throw new Exception("OggSが見つかりませんでした。");
		}
		// 次のbit8とbit5 bit1 bit1を取得する。
		Bit8 version = new Bit8();
		Bit5 zeroFill = new Bit5();
		Bit1 logicEndFlag = new Bit1();
		Bit1 logicStartFlag = new Bit1();
		Bit1 packetContinurousFlag = new Bit1();
		BitLoader loader = new BitLoader(channel);
		loader.load(version, zeroFill,
				logicEndFlag, logicStartFlag,
				packetContinurousFlag);
		// 必要なpage作成
		OggPage page = null;
		if(logicStartFlag.get() == 1) {
			page = new StartPage(version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag);
		}
		else {
			page = new Page(version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag);
		}
		page.minimumLoad(channel);
		return page;
	}
}
