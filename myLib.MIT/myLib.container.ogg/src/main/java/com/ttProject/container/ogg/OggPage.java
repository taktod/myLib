package com.ttProject.container.ogg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.Container;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * oggはoggPageというものができているらしい。
 * @author taktod
 */
public abstract class OggPage extends Container {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggPage.class);
	public static final String capturePattern = "OggS"; // 固定のはず
	private final Bit8 version;
	private final Bit5 zeroFill;
	private final Bit1 logicEndFlag;
	private final Bit1 logicStartFlag;
	private final Bit1 packetContinurousFlag;

	// ここから先はminimumLoadで実行すればよい
	private long absoluteGranulePosition;
	private int streamSerialNumber;
	private int pageSequenceNo;
	private int pageChecksum;
	private Bit8 segmentCount = new Bit8();
	private List<Bit8> segmentSizeList = new ArrayList<Bit8>();
	/**
	 * コンストラクタ
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public OggPage(Bit8 version, Bit5 zeroFill,
			Bit1 logicEndFlag, Bit1 logicStartFlag,
			Bit1 packetContinurousFlag) {
		this.version = version;
		this.zeroFill = zeroFill;
		this.logicEndFlag = logicEndFlag;
		this.logicStartFlag = logicStartFlag;
		this.packetContinurousFlag = packetContinurousFlag;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setPosition(channel.position() - 13);
		// データを読み込む
		ByteBuffer buffer = BufferUtil.safeRead(channel, 20);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		absoluteGranulePosition = buffer.getLong();
		streamSerialNumber = buffer.getInt();
		pageSequenceNo = buffer.getInt();
		pageChecksum = buffer.getInt();
		BitLoader loader = new BitLoader(channel);
		loader.load(segmentCount);
		int size = 0;
		for(int i = 0;i < segmentCount.get();i ++) {
			Bit8 segmentSize = new Bit8();
			loader.load(segmentSize);
			size += segmentSize.get();
			segmentSizeList.add(segmentSize);
		}
		super.setSize(size + channel.position() - getPosition());
	}
}
