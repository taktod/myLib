package com.ttProject.container.ogg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.Container;
import com.ttProject.container.ogg.type.StartPage;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
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
	private List<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
	private List<IFrame> frameList = new ArrayList<IFrame>();
	private StartPage startPage = null;
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
		super.setPosition(channel.position() - 6);
		logger.info(logicEndFlag);
		logger.info(logicStartFlag);
		logger.info(packetContinurousFlag);
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
	protected ByteBuffer getHeaderBuffer() {
		ByteBuffer result = ByteBuffer.allocate(27 + segmentCount.get());
		result.order(ByteOrder.LITTLE_ENDIAN);
		result.put(capturePattern.getBytes());
		BitConnector connector = new BitConnector();
		result.put(connector.connect(
				version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag
		));
		result.putLong(absoluteGranulePosition);
		result.putInt(streamSerialNumber);
		result.putInt(pageSequenceNo);
		result.putInt(pageChecksum);
		connector.feed(segmentCount);
		for(Bit8 bit : segmentSizeList) {
			connector.feed(bit);
		}
		result.put(connector.connect());
		result.flip();
		return result;
	}
	protected List<Bit8> getSegmentSizeList() {
		return segmentSizeList;
	}
	protected List<ByteBuffer> getBufferList() {
		return bufferList;
	}
	protected List<IFrame> getFrameList() {
		return frameList;
	}
	/**
	 * シリアル番号を応答する
	 * @return
	 */
	public Integer getStreamSerialNumber() {
		return streamSerialNumber;
	}
	public void setStartPage(StartPage startPage) {
		this.startPage = startPage;
	}
	protected StartPage getStartPage() {
		return startPage;
	}
}
