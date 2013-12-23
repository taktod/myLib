package com.ttProject.container.mpegts;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * mpegtsのpacketを解析します
 * @author taktod
 */
public class MpegtsPacketSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsPacketSelector.class);
	private final int patPid = 0x0000;
	private final int sdtPid = 0x0011;
	private Pat pat = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// headerの4byteを読み込めばどのtypeか解析ができるので、その4byteを読み込むようにする。
		if(channel.size() == channel.position()) {
			// データが最後まできているので処理することができない。
			return null;
		}
		Bit8 syncByte = new Bit8();
		Bit1 transportErrorIndicator = new Bit1();
		Bit1 payloadUnitStartIndicator = new Bit1();
		Bit1 transportPriority = new Bit1();
		Bit13 pid = new Bit13();
		Bit2 scramblingControl = new Bit2();
		Bit1 adaptationFieldExist = new Bit1();
		Bit1 payloadFieldExist = new Bit1();
		Bit4 continuityCounter = new Bit4();
		BitLoader loader = new BitLoader(channel);
		loader.load(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
		if(syncByte.get() != 0x47) {
			throw new Exception("syncBitが一致しません。");
		}
		MpegtsPacket packet = null;
		if(pid.get() == sdtPid) {
			logger.info("sdtデータ");
			packet = new Sdt(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
		}
		else if(pid.get() == patPid) {
			logger.info("patデータ");
			// patを保持しておく
			pat = new Pat(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
			packet = pat;
		}
		else if(pid.get() == pat.getPmtPid()){
			logger.info("pmtデータ");
			return null;
		}
		else {
			// esPidであるか確認
			// その他
			return null;
		}
		packet.minimumLoad(channel);
		return packet;
	}
}
