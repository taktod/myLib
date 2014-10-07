/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.aac.AacFrameAnalyzer;
import com.ttProject.frame.h264.NalAnalyzer;
import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
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
 * selector for mpegtsPacket
 * @author taktod
 */
public class MpegtsPacketSelector implements ISelector {
	/** logger */
	private Logger logger = Logger.getLogger(MpegtsPacketSelector.class);
	private final int patPid = 0x0000;
	private final int sdtPid = 0x0011;
	private Sdt sdt = null;
	private Pat pat = null;
	private Pmt pmt = null;
	private Map<Integer, Pes> pesMap = new HashMap<Integer, Pes>();
	private Map<Integer, IAnalyzer> analyzerMap = new HashMap<Integer, IAnalyzer>();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() == channel.position()) {
			// no more information.
			return null;
		}
		// read first 4 byte.(mpegts packet header.)
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
			throw new Exception("syncBit is invalid.");
		}
		MpegtsPacket packet = null;
		if(pid.get() == sdtPid) {
			Sdt tmpSdt = new Sdt(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
			tmpSdt.minimumLoad(channel);
			if(sdt == null || sdt.getCrc() != tmpSdt.getCrc()) {
				// is sdt is null or crc is different, update with new sdt.
				sdt = tmpSdt;
			}
			return sdt;
		}
		else if(pid.get() == patPid) {
			// hold pat information.
			Pat tmpPat = new Pat(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
			tmpPat.minimumLoad(channel);
			if(pat == null || pat.getCrc() != tmpPat.getCrc()) {
				pat = tmpPat;
			}
			return pat;
		}
		else if(pat != null && pid.get() == pat.getPmtPid()){
			Pmt tmpPmt = new Pmt(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
			tmpPmt.minimumLoad(channel);
			if(pmt == null || pmt.getCrc() != tmpPmt.getCrc()) {
				pmt = tmpPmt;
			}
			else {
				return pmt;
			}
			// in order to initialize elementaryField, need to load first.
			pmt.load(channel);
			// make analyzer.
			for(PmtElementaryField elementaryField : pmt.getFields()) {
				logger.info(elementaryField.getCodecType());
				switch(elementaryField.getCodecType()) {
				case AUDIO_AAC:
					analyzerMap.put((int)elementaryField.getPid(), new AacFrameAnalyzer());
					break;
				case AUDIO_MPEG1:
					analyzerMap.put((int)elementaryField.getPid(), new Mp3FrameAnalyzer());
					break;
				case VIDEO_H264:
					analyzerMap.put((int)elementaryField.getPid(), new NalAnalyzer());
					break;
				default:
					break;
				}
			}
			return pmt;
		}
		else if(pmt != null && pmt.isPesPid(pid.get())) {
			Pes pes = new Pes(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter, pmt.getPcrPid() == pid.get());
			if(payloadUnitStartIndicator.get() == 1) {
				// if unit is start again. commit the previous one.
				pesMap.put(pid.get(), pes);
				pes.setFrameAnalyzer(analyzerMap.get(pid.get()));
			}
			else {
				pes.setUnitStartPes(pesMap.get(pid.get()));
			}
			packet = pes;
		}
		else {
			logger.info("other data." + Integer.toHexString(pid.get()));
			return null;
		}
		packet.minimumLoad(channel);
		return packet;
	}
}
