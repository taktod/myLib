/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg;

import java.nio.ByteBuffer;
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
 * basic of oggPage
 * @author taktod
 * TODO theora is not tested yet.
 */
public abstract class OggPage extends Container {
	/** logger */
	private Logger logger = Logger.getLogger(OggPage.class);
	public static final String capturePattern = "OggS"; // fixed?

//	private final Bit32 syncString;
	private final Bit1  packetContinurousFlag;
	private final Bit1  logicStartFlag;
	private final Bit1  logicEndFlag;
	private final Bit5  zeroFill;
	private final Bit8  version;

	private Bit64 absoluteGranulePosition = new Bit64();
	private Bit32 streamSerialNumber      = new Bit32();
	private Bit32 pageSequenceNo          = new Bit32();
	private Bit32 pageChecksum            = new Bit32();
	private Bit8  segmentCount            = new Bit8();

	private List<Bit8> segmentSizeList = new ArrayList<Bit8>();
	private List<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
	private List<IFrame> frameList = new ArrayList<IFrame>(); // TODO should I use multiFrame?
	private StartPage startPage = null;
	/**
	 * constructor
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
//		this.syncString = new Bit32('O' | ('g' << 8) | ('g' << 16) | ('S' << 24));
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
	 * ref the header buffer
	 * @return
	 */
	protected ByteBuffer getHeaderBuffer() {
//		logger.info(frameList);
		segmentCount.set(frameList.size());
		logger.info("defList:" + segmentSizeList.size());
		logger.info(frameList.size());
		for(IFrame frame : frameList) {
			segmentSizeList.add(new Bit8(frame.getSize()));
		}
		logger.info("headerSize:" + (27 + segmentCount.get()));
		BitConnector connector = new BitConnector();
		connector.setLittleEndianFlg(true);
		connector.feed(new Bit8('O'), new Bit8('g'), new Bit8('g'), new Bit8('S'), version, packetContinurousFlag, logicStartFlag, logicEndFlag, zeroFill, 
				absoluteGranulePosition, streamSerialNumber, pageSequenceNo, pageChecksum, segmentCount);
		int size = 0;
		for(Bit8 bit : segmentSizeList) {
			connector.feed(bit);
			size += bit.get();
		}
		super.setSize(27 + segmentCount.get() + size);
		logger.info("dataSize:" + getSize());
		return connector.connect();// */
/*
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
		
		return result;// */
	}
	/**
	 * ref of segment size list.
	 * @return
	 */
	protected List<Bit8> getSegmentSizeList() {
		return segmentSizeList;
	}
	/**
	 * ref of buffers
	 * @return
	 */
	protected List<ByteBuffer> getBufferList() {
		return bufferList;
	}
	/**
	 * ref analyzed frame list.
	 * @return
	 */
	public List<IFrame> getFrameList() {
		return frameList;
	}
	/**
	 * ref stream serial number.
	 * @return
	 */
	public Integer getStreamSerialNumber() {
		return streamSerialNumber.get();
	}
	/**
	 * set startPage object.
	 * @param startPage
	 */
	public void setStartPage(StartPage startPage) {
		logger.info("setStartPage:" + startPage);
		this.startPage = startPage;
	}
	/**
	 * ref startPage
	 * @return
	 */
	protected StartPage getStartPage() {
		return startPage;
	}
	/**
	 * ref page sequence num.
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
	public long getAbsoluteGranulePosition() {
		return absoluteGranulePosition.getLong();
	}
}
