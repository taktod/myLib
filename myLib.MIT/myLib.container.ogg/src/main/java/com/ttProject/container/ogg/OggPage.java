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
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * oggはoggPageというものができているらしい。
 * @author taktod
 */
public abstract class OggPage extends Container {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggPage.class);
	public static final String capturePattern = "OggS"; // 固定のはず

	private final Bit32 syncString;
	private final Bit1  packetContinurousFlag;
	private final Bit1  logicStartFlag;
	private final Bit1  logicEndFlag;
	private final Bit5  zeroFill;
	private final Bit8  version;

	// ここから先はminimumLoadで実行すればよい bit数に書き直したいけど、littleEndianの取り扱いが微妙
	// でもこの部分はbyteが逆向きではいっているので、Bit動作としては、ただしくない動作になってしまう。
	private Bit64 absoluteGranulePosition = new Bit64();
	private Bit32 streamSerialNumber      = new Bit32();
	private Bit32 pageSequenceNo          = new Bit32();
	private Bit32 pageChecksum            = new Bit32();
	private Bit8  segmentCount            = new Bit8();

	private List<Bit8> segmentSizeList = new ArrayList<Bit8>();
	private List<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
	private List<IFrame> frameList = new ArrayList<IFrame>(); // ここ、あとでmultiFrameを使う方がいいとわかったら変更したいところ。
	private StartPage startPage = null;
	/**
	 * コンストラクタ
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public OggPage(Bit8 version,
			Bit1 packetContinurousFlag,
			Bit1 logicStartFlag,
			Bit1 logicEndFlag,
			Bit5 zeroFill) {
		this.syncString = new Bit32(('O' << 24) | ('g' << 16) | ('g' << 8) | 'S');
		this.version = version;
		this.zeroFill = zeroFill;
		this.logicEndFlag = logicEndFlag;
		this.logicStartFlag = logicStartFlag;
		this.packetContinurousFlag = packetContinurousFlag;
		super.update();
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
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(absoluteGranulePosition, streamSerialNumber, pageSequenceNo, pageChecksum, segmentCount);
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
//		logger.info(frameList);
		// ここのデータはframeListから作る必要があるわけか・・・
		segmentCount.set(frameList.size());
		logger.info("defList:" + segmentSizeList.size());
		logger.info(frameList.size());
		for(IFrame frame : frameList) {
			segmentSizeList.add(new Bit8(frame.getSize()));
		}
		logger.info("headerSize:" + (27 + segmentCount.get()));
		ByteBuffer result = ByteBuffer.allocate(27 + segmentCount.get());
		result.order(ByteOrder.LITTLE_ENDIAN);
		BitConnector connector = new BitConnector();
		result.put(connector.connect(
				syncString, version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag
		));
		result.putLong(absoluteGranulePosition.getLong());
		result.putInt(streamSerialNumber.get());
		result.putInt(pageSequenceNo.get());
		result.putInt(pageChecksum.get());
		connector.feed(segmentCount);
		logger.info("segmentCount:" + segmentCount.get());
		logger.info(segmentSizeList.size());
		int size = 0;
		for(Bit8 bit : segmentSizeList) {
//			logger.info(bit.get());
			connector.feed(bit);
			size += bit.get();
		}
		result.put(connector.connect());
		result.flip();
		super.setSize(result.remaining() + size);
		logger.info("dataSize:" + (result.remaining() + size));
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
		return streamSerialNumber.get();
	}
	/**
	 * startPageを保持設定しておく。
	 * @param startPage
	 */
	public void setStartPage(StartPage startPage) {
		logger.info("setStartPage:" + startPage);
		this.startPage = startPage;
	}
	/**
	 * startPageを参照
	 * @return
	 */
	protected StartPage getStartPage() {
		return startPage;
	}
	/**
	 * pageの番号を参照する
	 * @return
	 */
	public int getPageSequenceNo() {
		return pageSequenceNo.get();
	}
	public void setAbsoluteGranulePosition(long granulePosition) {
		absoluteGranulePosition.setLong(granulePosition);
	}
	public void setStreamSerialNumber(int serialNumber) {
		streamSerialNumber.set(serialNumber);
	}
	public void setPageSequenceNo(int sequenceNo) {
		pageSequenceNo.set(sequenceNo);
	}
	public void setLogicEndFlag(boolean flag) {
		if(flag) {
			logicEndFlag.set(1);
		}
		else {
			logicEndFlag.set(0);
		}
	}
}
