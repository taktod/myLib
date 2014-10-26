/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;

/**
 * mpegts packet writer.
 * hold the sdt pat pmt.
 * these three type of packet will be written with keyFrame or (non video data) fixed time interval.
 * 
 * think about 2 type. audioOnly and audio + video.
 * audioOnly -> 1sec for each pes.
 * videoOnly -> each frame is each pes.
 * need to setup invidually.
 * @author taktod
 */
public class MpegtsPacketWriter implements IWriter {
	/** logger */
	private Logger logger = Logger.getLogger(MpegtsPacketWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	
	/** continuityCounterMap */
	private Map<Integer, Integer> continuityCounterMap = new HashMap<Integer, Integer>();
	/** sdt */
	private Sdt sdt = null;
	/** pat */
	private Pat pat = null;
	/** pmt */
	private Pmt pmt = null;
	/** make pes from frame. */
	private FrameToPesConverter converter = new FrameToPesConverter();
	/** flag for written first metadata. */
	private boolean isWriteFirstMeta = false;
	/**
	 * constructor
	 * @param fileName
	 * @throws Exception
	 */
	public MpegtsPacketWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public MpegtsPacketWriter(FileOutputStream fileOutputStream) {
		this(fileOutputStream.getChannel());
	}
	public MpegtsPacketWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
		if(container instanceof Sdt) {
			sdt = (Sdt)container;
		}
		else if(container instanceof Pat) {
			pat = (Pat)container;
		}
		else if(container instanceof Pmt) {
			pmt = (Pmt)container;
		}
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		if(frame == null) {
			return;
		}
		if(frame instanceof VideoMultiFrame) {
			VideoMultiFrame multiFrame = (VideoMultiFrame) frame;
			for(IVideoFrame vFrame : multiFrame.getFrameList()) {
				addFrame(trackId, vFrame);
			}
			return;
		}
		if(frame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame) frame;
			for(IAudioFrame aFrame : multiFrame.getFrameList()) {
				addFrame(trackId, aFrame);
			}
			return;
		}
		if(!isWriteFirstMeta) {
			// first written, write sdt pat pmt.
			if(sdt == null || pat == null || pmt == null) {
				throw new Exception("sdt pat pmt is expected for writing.");
			}
			writeMpegtsPacket(sdt);
			writeMpegtsPacket(pat);
			writeMpegtsPacket(pmt);
			isWriteFirstMeta = true;
		}
		Pes pes = converter.getPeses(trackId, pmt, frame);
		if(pes == null) {
			return;
		}
		writeMpegtsPacket(pes);
	}
	private void writeMpegtsPacket(MpegtsPacket packet) throws Exception {
		Integer counter = continuityCounterMap.get(packet.getPid());
		if(counter == null) {
			counter = 0;
		}
		packet.setContinuityCounter(counter);
		outputChannel.write(packet.getData());
		continuityCounterMap.put(packet.getPid(), packet.getContinuityCounter() + 1);
	}
	@Override
	public void prepareHeader(CodecType ...codecs) throws Exception {
		/*
		 * try to make the tracks and keep the trackId -> codecType maps
		 * after, cause of the trackId from previous container.
		 * pre trackId -> trackId
		 * if no information for pre trackId, find the same codec track.
		 * and assign the id.
		 * ここでcodecType -> pidリストをつくっておく
		 * フレームアクセスがあったら
		 * すでにtrackId -> pidのマップがあるならそれを利用
		 * trackIdに対応したpidがわからないなら
		 * pidを調べる
		 * 入力フレームと同じcodecTypeのデータを探して一致するものが・・・
		 * この方法だと同じコーデックのトラックが複数ある場合は動作できないか・・・
		 * 
		 * 逆にpid -> codecTypeをいれておいて、
		 * あたらしいフレームをみつけたら、前からpidを確認していって
		 * 該当のpidをみつける形にしておこう。
		 */
	}
	@Override
	public void prepareTailer() throws Exception {
		Map<Integer, Pes> remainMap = converter.getPesMap();
		// remained all pes will be written.
		for(Entry<Integer, Pes> entry : remainMap.entrySet()) {
			Pes pes = entry.getValue();
			try {
				writeMpegtsPacket(pes);
			}
			catch(Exception e) {
				logger.error(e);
			}
		}
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {
			}
			outputStream = null;
		}
	}
}
