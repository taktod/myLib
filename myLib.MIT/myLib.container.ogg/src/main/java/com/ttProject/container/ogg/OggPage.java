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
import com.ttProject.unit.extra.bit.Bit32;
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

	private final Bit32 syncString;
	private final Bit8  version;
	private final Bit5  zeroFill;
	private final Bit1  logicEndFlag;
	private final Bit1  logicStartFlag;
	private final Bit1  packetContinurousFlag;

	// ここから先はminimumLoadで実行すればよい bit数に書き直したいけど、littleEndianの取り扱いが微妙
	private long absoluteGranulePosition; // TODO bit数に書き直したい
	private int streamSerialNumber; // TODO bit数に書き直したい
	private int pageSequenceNo; // TODO bit数に書き直したい
	private int pageChecksum; // TODO bit数に書き直したい
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
		this.syncString = new Bit32(('O' << 24) | ('g' << 16) | ('g' << 8) | 'S');
		this.version = version;
		this.zeroFill = zeroFill;
		this.logicEndFlag = logicEndFlag;
		this.logicStartFlag = logicStartFlag;
		this.packetContinurousFlag = packetContinurousFlag;
	}
	/**
	 * {@inheritDoc}
	 */
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
		logger.info(segmentCount.get());
		int size = 0;
		for(int i = 0;i < segmentCount.get();i ++) {
			Bit8 segmentSize = new Bit8();
			loader.load(segmentSize);
			size += segmentSize.get();
			segmentSizeList.add(segmentSize);
		}
		super.setSize(size + channel.position() - getPosition());
	}
	/**
	 * headerにあるデータbufferを応答します。
	 * @return
	 */
	protected ByteBuffer getHeaderBuffer() {
		ByteBuffer result = ByteBuffer.allocate(27 + segmentCount.get());
		result.order(ByteOrder.LITTLE_ENDIAN);
//		result.put(capturePattern.getBytes());
		BitConnector connector = new BitConnector();
		result.put(connector.connect(
				syncString, version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag
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
	/**
	 * 内部保持segmentSizeリストの参照
	 * @return
	 */
	protected List<Bit8> getSegmentSizeList() {
		return segmentSizeList;
	}
	/**
	 * 内部保持bufferリスト参照
	 * @return
	 */
	protected List<ByteBuffer> getBufferList() {
		return bufferList;
	}
	/**
	 * 解析済みフレームリスト参照
	 * @return
	 */
	public List<IFrame> getFrameList() {
		return frameList;
	}
	/**
	 * シリアル番号を応答する
	 * @return
	 */
	public Integer getStreamSerialNumber() {
		return streamSerialNumber;
	}
	/**
	 * startPageを保持設定しておく。
	 * @param startPage
	 */
	public void setStartPage(StartPage startPage) {
		this.startPage = startPage;
	}
	/**
	 * startPageを参照
	 * @return
	 */
	protected StartPage getStartPage() {
		return startPage;
	}
}
