/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.ProgramPacket;
import com.ttProject.container.mpegts.descriptor.Descriptor;
import com.ttProject.container.mpegts.descriptor.ServiceDescriptor;
import com.ttProject.container.mpegts.field.SdtServiceField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * Sdt(Service Description Table)
 * sample.
 * 474011100042F0240001C100000001FF 0001FC8013481101054C696261760953657276696365303168C5DB49
 * 47 40 11 10  mpegtsPacketHeader
 * 00 42 F0 24 00 01 C1 00 00  programPacket
 * 00 01 FF  sdt
 * 00 01 FC 80 13  sdtServiceField
 * 48 11  descriptor
 * 01 05 [4C 69 62 61 76] 09 [53 65 72 76 69 63 65 30 31]  ServiceDescriptor
 * 68 C5 DB 49  crc32
 * @see http://en.wikipedia.org/wiki/Service_Description_Table
 * @see http://pda.etsi.org/exchangefolder/en_300468v011301p.pdf
 * ここでは、サービスごとに、descriptorが複数持てるようになっているみたいです。
 * よって、sdtにデータをいれる場合は、serviceIdとdescriptorのデータを指定していれる必要があると思われます。
 * @author taktod
 */
public class Sdt extends ProgramPacket {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Sdt.class);
	private Bit16 originalNetworkId  = null;
	private Bit8  reservedFutureUse2 = null;
	private List<SdtServiceField> serviceFields = new ArrayList<SdtServiceField>();
	private Bit32 crc32 = new Bit32();
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
	public Sdt(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
		super.update();
	}
	/**
	 * constructor
	 */
	public Sdt() {
		this(new Bit8(0x47), new Bit1(),
				new Bit1(1), new Bit1(),
				new Bit13(0x11), new Bit2(), new Bit1(),
				new Bit1(1), new Bit4());
		// need to load here.
		try {
			super.load(new ByteReadChannel(new byte[]{
				0x00, 0x42, (byte)0xF0, 0x24, 0x00, 0x01, (byte)0xC1, 0x00, 0x00,
			}));
			originalNetworkId = new Bit16(1);
			reservedFutureUse2 = new Bit8(0xFF);
		}
		catch(Exception e) {
		}
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(crc32);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(isLoaded()) {
			return;
		}
		IReadChannel holdChannel = new ByteReadChannel(getBuffer());
		super.load(holdChannel);
		BitLoader loader = new BitLoader(holdChannel);
		originalNetworkId  = new Bit16();
		reservedFutureUse2 = new Bit8();
		loader.load(originalNetworkId, reservedFutureUse2);
		int size = getSectionLength() - 8 - 4;
		while(size > 0) {
			SdtServiceField ssfield = new SdtServiceField();
			ssfield.load(holdChannel);
			size -= ssfield.getSize();
			serviceFields.add(ssfield);
		}
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		connector.feed(originalNetworkId, reservedFutureUse2);
		for(SdtServiceField ssField : serviceFields) {
			connector.feed(ssField.getBits());
		}
		ByteBuffer tmpBuffer = BufferUtil.connect(
				getHeaderBuffer(),
				connector.connect()
		);
		// crc32 is required.
		int crc32 = calculateCrc(tmpBuffer);
		this.crc32.set(crc32);
		ByteBuffer buffer = ByteBuffer.allocate(188);
		buffer.put(tmpBuffer);
		buffer.putInt(crc32);
		// fill til 188 bytes
		while(buffer.position() < 188) {
			buffer.put((byte)0xFF);
		}
		buffer.flip();
		super.setData(buffer);
	}
	/**
	 * write default provider.
	 * @param provider
	 * @param name
	 */
	public void writeDefaultProvider(String provider, String name) {
		// check other service field.
		SdtServiceField targetField = null;
		for(SdtServiceField ssField : serviceFields) {
			if(ssField.getServiceId() == 1) {
				targetField = ssField;
				break;
			}
		}
		// if none, add
		if(targetField == null) {
			targetField = new SdtServiceField();
			serviceFields.add(targetField);
		}
		// check descriptor
		ServiceDescriptor targetDescriptor = null;
		for(Descriptor descriptor : targetField.getDescriptors()) {
			if(descriptor instanceof ServiceDescriptor) {
				targetDescriptor = (ServiceDescriptor)descriptor;
				break;
			}
		}
		// if none, add.
		if(targetDescriptor == null) {
			targetDescriptor = new ServiceDescriptor(targetField);
			targetField.addDescriptor(targetDescriptor);
		}
		targetDescriptor.setName(provider, name);
		// update length
		short length = 0;
		// sectionlength ... and programPacket
		length += 5;
		// sdt
		length += 3;
		// sdtServiceField
		for(SdtServiceField ssField : serviceFields) {
			length += ssField.getSize();
		}
		// crc32
		length += 4;
		setSectionLength(length);
		// update flag.
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCrc() {
		return crc32.get();
	}
}
