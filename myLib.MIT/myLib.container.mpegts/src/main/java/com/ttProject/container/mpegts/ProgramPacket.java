/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * programPacket
 * @author taktod
 * 
 * TODO for program packet, if the crc32 is same, no need to write.
 * this will help to reduce the media size.
 */
public abstract class ProgramPacket extends MpegtsPacket {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(ProgramPacket.class);
	private Bit8  pointerField           = null; // 0000 0000
	private Bit8  tableId                = null; // packet fixed value
	private Bit1  sectionSyntaxIndicator = null; // 1
	private Bit1  reservedFutureUse1     = null; // 0
	private Bit2  reserved1              = null; // 11
	private Bit12 sectionLength          = null; // 12bit
	private Bit16 programNumber          = null; // 16bit
	private Bit2  reserved2              = null; // 11
	private Bit5  versionNumber          = null; // 00000
	private Bit1  currentNextOrder       = null; // 1
	private Bit8  sectionNumber          = null; // 00000000
	private Bit8  lastSectionNumber      = null; // 00000000
	
	/** original data */
	private ByteBuffer buffer = null;
	private boolean isLoaded = false;
	/**
	 * constructor
	 * @param syncByte
	 * @param transportErrorIndicator
	 * @param payloadUnitStartIndicator
	 * @param transportPriority
	 * @param pid
	 * @param scramblingControl
	 * @param adaptationFieldExist
	 * @param payloadFieldExist
	 * @param continuityCounter
	 */
	public ProgramPacket(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		int bufferLength = 180; // last 4byte(crc32) is not consider here.(lastly, we add crc32.)
		if(isAdaptationFieldExist()) {
			bufferLength -= (1 + getAdaptationField().getLength());
		}
		buffer = BufferUtil.safeRead(channel, bufferLength);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		pointerField           = new Bit8();
		tableId                = new Bit8();
		sectionSyntaxIndicator = new Bit1();
		reservedFutureUse1     = new Bit1();
		reserved1              = new Bit2();
		sectionLength          = new Bit12();
		programNumber          = new Bit16();
		reserved2              = new Bit2();
		versionNumber          = new Bit5();
		currentNextOrder       = new Bit1();
		sectionNumber          = new Bit8();
		lastSectionNumber      = new Bit8();
		loader.load(pointerField, tableId, sectionSyntaxIndicator,
				reservedFutureUse1, reserved1, sectionLength, programNumber,
				reserved2, versionNumber, currentNextOrder,
				sectionNumber, lastSectionNumber);
		if(isAdaptationFieldExist()) {
			super.setSize(8 + getAdaptationField().getLength() + 1 + sectionLength.get());
		}
		else {
			super.setSize(8 + sectionLength.get());
		}
		isLoaded = true;
	}
	/**
	 * set sectionLength
	 * @param length
	 */
	protected void setSectionLength(int length) {
		// sectionLength will be changed later, need to set.
		sectionLength.set(length);
		super.setSize(8 + sectionLength.get());
	}
	/**
	 * ref sectionLength
	 * @return
	 */
	protected int getSectionLength() {
		return sectionLength.get();
	}
	/**
	 * ref buffer
	 * @return
	 */
	protected ByteBuffer getBuffer() {
		return buffer;
	}
	/**
	 * loaded?
	 * @return
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return BufferUtil.connect(
			super.getHeaderBuffer(),
			connector.connect(
				pointerField, tableId, sectionSyntaxIndicator, reservedFutureUse1,
				reserved1, sectionLength, programNumber, reserved2, versionNumber,
				currentNextOrder, sectionNumber, lastSectionNumber
			)
		);
	}
	/**
	 * ref the crc.
	 * @return
	 */
	public abstract int getCrc();
	/**
	 * calculate crc
	 * @param buffer
	 * @return
	 */
	protected int calculateCrc(ByteBuffer buffer) {
		// TODO this calcuration will be work but not correct.
		// in the case of adaptation Field is exist on the top, won't work.
		Crc32 crc32 = new Crc32();
		ByteBuffer tmpBuffer = buffer.duplicate();
		tmpBuffer.position(5); // consider no adaptation field.
		while(tmpBuffer.remaining() > 0) {
			crc32.update(tmpBuffer.get());
		}
		return (int)crc32.getValue();
	}
}
