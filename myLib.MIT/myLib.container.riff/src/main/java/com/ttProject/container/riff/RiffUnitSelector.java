/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.ttProject.container.riff.type.Avih;
import com.ttProject.container.riff.type.Data;
import com.ttProject.container.riff.type.Dc;
import com.ttProject.container.riff.type.Fact;
import com.ttProject.container.riff.type.Fmt;
import com.ttProject.container.riff.type.Hdrl;
import com.ttProject.container.riff.type.Info;
import com.ttProject.container.riff.type.Isft;
import com.ttProject.container.riff.type.Junk;
import com.ttProject.container.riff.type.List;
import com.ttProject.container.riff.type.Movi;
import com.ttProject.container.riff.type.Riff;
import com.ttProject.container.riff.type.Strf;
import com.ttProject.container.riff.type.Strh;
import com.ttProject.container.riff.type.Strl;
import com.ttProject.container.riff.type.Vprp;
import com.ttProject.container.riff.type.Wb;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

/**
 * riff unit selector
 * NOTE there are multi riff avi.... should I support?
 * @author taktod
 */
public class RiffUnitSelector implements ISelector {
	/** format information */
	private java.util.List<RiffFormatUnit> formatUnitList = new ArrayList<RiffFormatUnit>();
	private Strh prevStrhUnit = null;
	// remain data size in the data tag.
	private long dataRemainLength = -1;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		RiffUnit unit = null;
		if(dataRemainLength > 0) {
			int blockSize = 0;
			RiffFormatUnit formatUnit = formatUnitList.get(0);
			switch (formatUnit.getCodecType()) {
			case PCM_ALAW:
			case PCM_MULAW:
				blockSize = 0x0100;
				if(dataRemainLength < 0x0100) {
					blockSize = (int)dataRemainLength;
				}
				break;
			default:
				blockSize = formatUnit.getBlockSize();
				break;
			}
			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.put((byte)0x30);
			buffer.put((byte)0x30);
			buffer.put((byte)((Type.wb.intValue() &0x0000FF00) >> 8));
			buffer.put((byte)(Type.wb.intValue() &0x000000FF));
			buffer.putInt(blockSize);
			buffer.flip();
			buffer.order(ByteOrder.BIG_ENDIAN);
			channel = new ByteReadChannel(BufferUtil.connect(buffer, BufferUtil.safeRead(channel, blockSize)));
		}
		// check first 4byte
		// sometimes old ffmpeg has a bug to put the extra 0x00 on the data.
		// we need to ignore this.
		int firstByte = -1;
		while((firstByte = BufferUtil.safeRead(channel, 1).get()) == 0) {
			// skip 0x00
		}
		ByteBuffer buffer = BufferUtil.safeRead(channel, 3);
		int typeValue = (firstByte & 0xFF) << 24;
		typeValue |= (buffer.get() & 0xFF) << 16;
		typeValue |= (buffer.get() & 0xFF) << 8;
		typeValue |= (buffer.get() & 0xFF);
		Type type = Type.getType(typeValue);
		switch(type) {
		case RIFF: // header
			if(channel.position() != 4) {
				throw new Exception("position of header is invalid.");
			}
			unit = new Riff();
			break;
		case FMT: // format information(must)
			{
				RiffFormatUnit formatUnit = new Fmt();
				formatUnitList.add(formatUnit);
				if(prevStrhUnit != null) {
					// maybe no way to come here..
					formatUnit.setRate(prevStrhUnit.getRate());
					formatUnit.setScale(prevStrhUnit.getScale());
				}
				unit = formatUnit;
			}
			break;
		case FACT: // sampleNum and so on...
			unit = new Fact();
			break;
		case DATA: // data body.(must)
			unit = new Data();
			break;
		case LIST: // ?
			unit = new List();
			break;
		case hdrl:
			unit = new Hdrl();
			break;
		case avih:
			unit = new Avih();
			break;
		case strl:
			unit = new Strl();
			break;
		case strh:
			unit = new Strh();
			prevStrhUnit = (Strh)unit;
			break;
		case strf:
			{
				// check strh, if
				RiffFormatUnit formatUnit = null;
				switch(prevStrhUnit.getFccType()) {
				case auds:
					// use fmt
					formatUnit = new Fmt();
					break;
				case mids:
				case tets:
					throw new Exception("unknown for mids or tets.");
				case vids:
					formatUnit = new Strf(prevStrhUnit.getRiffCodecType());
					break;
				}
				formatUnitList.add(formatUnit);
				if(prevStrhUnit != null) {
					formatUnit.setRate(prevStrhUnit.getRate());
					formatUnit.setScale(prevStrhUnit.getScale());
				}
				unit = formatUnit;
			}
			break;
		case INFO:
			unit = new Info();
			break;
		case ISFT:
			unit = new Isft();
			break;
		case movi:
			unit = new Movi();
			break;
		case vprp:
			unit = new Vprp();
			break;
		case dc:
			unit = new Dc(typeValue);
			break;
		case wb:
			unit = new Wb(typeValue);
			break;
		case JUNK:
			unit = new Junk();
			break;
		default:
			throw new RuntimeException("unexpected frame type.:" + type);
		}
		if(unit == null) {
			throw new Exception("unit is undefined.maybe non-support type.:" + type);
		}
		if(unit instanceof RiffFrameUnit) {
			RiffFrameUnit frameUnit = (RiffFrameUnit)unit;
			frameUnit.setFormatUnit(formatUnitList.get(frameUnit.getTrackId()));
		}
		unit.minimumLoad(channel);
		if(unit instanceof Data) {
			dataRemainLength = unit.getSize() - 8;
		}
		return unit;
	}
}
