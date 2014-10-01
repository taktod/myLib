/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.type.ContentCompression;
import com.ttProject.container.mkv.type.ContentEncoding;
import com.ttProject.container.mkv.type.ContentEncodings;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.frame.Frame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * blockTagの共通部分
 * @author taktod
 */
public abstract class MkvBlockTag extends MkvBinaryTag {
	/** コンストラクタ */
	private Logger logger = Logger.getLogger(MkvBlockTag.class);
	private EbmlValue trackId       = new EbmlValue();
	private Bit16     timestampDiff = new Bit16();
	private long time = 0;
	private IFrame frame = null;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvBlockTag(Type id, EbmlValue size) {
		super(id, size);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(trackId, timestampDiff);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.load(channel);
		time = getMkvTagReader().getClusterTime() + timestampDiff.get();
	}
	/**
	 * lacingTypeの参照
	 * @return
	 */
	protected abstract Lacing getLacingType() throws Exception;
	/**
	 * フレームを参照する
	 * @return
	 */
	public IFrame getFrame() throws Exception {
		if(frame == null) {
			analyzeFrames();
		}
		return frame;
	}
	/**
	 * フレームを解析する
	 * @throws Exception
	 */
	private void analyzeFrames() throws Exception {
		IReadChannel channel = null;
		try {
			// lacingについて調べておく
			List<Integer> lacingSizeList = new ArrayList<Integer>();
			channel = new ByteReadChannel(getMkvData());
			switch(getLacingType()) {
			case No:
				// 単純に残りデータ全部
				lacingSizeList.add(channel.size());
				break;
			case Xiph:
				{
					Bit8 num = new Bit8();
					BitLoader loader = new BitLoader(channel);
					loader.load(num);
					int size = 0;
					for(int i = 0;i < num.get();i ++) {
						Bit8 sizeByte = null;
						do {
							sizeByte = new Bit8();
							loader.load(sizeByte);
							size += sizeByte.get();
						} while(sizeByte.get() == 0xFF);
						lacingSizeList.add(size);
						size = 0;
					}
				}
				break;
			case EBML:
				{
					Bit8 num = new Bit8();
					BitLoader loader = new BitLoader(channel);
					loader.load(num);
					int size = 0;
					for(int i = 0;i < num.get();i ++) {
						EbmlValue value = new EbmlValue();
						loader.load(value);
						if(i == 0) {
							size = value.get();
						}
						else {
							// 差分を計算する。
							int diff = value.get() - ((1 << (7 * value.getBitCount() / 8 - 1)) - 1);
							size = size + diff;
						}
						lacingSizeList.add(size);
					}
				}
				break;
			case FixedSize:
				{
					Bit8 num = new Bit8();
					BitLoader loader = new BitLoader(channel);
					loader.load(num);
					// num + 1の数でのこりのデータ量を分割したのが、lacingSize
					int lacingSize = (channel.size() - 1) / (num.get() + 1);
					for(int i = 0;i < num.get() + 1;i ++) {
						lacingSizeList.add(lacingSize);
					}
				}
				break;
			default:
				throw new Exception("lacing type is corrupted.");
			}
			// frameデータを調整したい。
			TrackEntry entry = getMkvTagReader().getTrackEntry(trackId.get());
			ContentEncodings encodings = entry.getEncodings();
			// TODO この書き方だと、lacing対策していないので、調整する必要あり。
			if(encodings == null) {
				for(Integer size : lacingSizeList) {
					analyzeFrame(entry, new ByteReadChannel(BufferUtil.safeRead(channel, size)));
				}
			}
			else {
				for(MkvTag tag : encodings.getChildList()) {
					if(tag instanceof ContentEncoding) {
						ContentEncoding encoding = (ContentEncoding)tag;
						logger.info(encoding);
						for(MkvTag etag : encoding.getChildList()) {
							if(etag instanceof ContentCompression) {
								ContentCompression compression = (ContentCompression)etag;
								logger.info(compression);
								switch(compression.getAlgoType()) {
								case Zlib:
									throw new Exception("zlib is not supported, I need sample.");
								case HeaderStripping:
									logger.info("header stripping...");
									for(Integer size : lacingSizeList) {
										analyzeFrame(entry, new ByteReadChannel(BufferUtil.connect(compression.getSettingData(), BufferUtil.safeRead(channel, size))));
									}
									break;
								default:
									throw new Exception("this algo type is not supported:" + compression.getAlgoType());
								}
							}
							else {
								throw new Exception("unknown encoding data is founded.:" + etag);
							}
						}
					}
					else {
						throw new Exception("contentEncodings has unexpected data.:" + tag);
					}
				}
			}
		}
		finally {
			if(channel != null) {
				channel.close();
				channel = null;
			}
		}
	}
	/**
	 * フレームを解析して追加する
	 * @param entry
	 * @param channel
	 * @throws Exception
	 */
	private void analyzeFrame(TrackEntry entry, IReadChannel channel) throws Exception {
		IAnalyzer analyzer = entry.getAnalyzer();
		IFrame analyzedFrame = null;
		do {
			analyzedFrame = analyzer.analyze(channel);
			if(analyzedFrame == null) {
				throw new Exception("no frame is analyzed");
			}
			if(analyzedFrame instanceof NullFrame || !(analyzedFrame instanceof Frame)) {
				continue;
			}
			Frame tmpFrame = (Frame)analyzedFrame;
			tmpFrame.setPts(time);
			tmpFrame.setTimebase(entry.getTimebase());
			addFrame(tmpFrame);
		} while(channel.size() != channel.position());
		// のこっているデータがある場合は解析しなければならない。
		analyzedFrame = analyzer.getRemainFrame();
		if(analyzedFrame != null && !(analyzedFrame instanceof NullFrame) && analyzedFrame instanceof Frame) {
			Frame tmpFrame = (Frame)analyzedFrame;
			tmpFrame.setPts(time);
			tmpFrame.setTimebase(entry.getTimebase());
			addFrame(tmpFrame);
		}
	}
	/**
	 * フレームを追加する。
	 * @param tmpFrame
	 * @throws Exception
	 */
	protected void addFrame(IFrame tmpFrame) throws Exception {
		if(tmpFrame == null) {
			return;
		}
		if(frame == null) {
			frame = tmpFrame;
		}
		else if(frame instanceof AudioMultiFrame) {
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("try to add non-audioFrame for audioMultiFrame.");
			}
			((AudioMultiFrame)frame).addFrame((IAudioFrame)tmpFrame);
		}
		else if(frame instanceof VideoMultiFrame) {
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("try to add non-videoFrame for videoMultiFrame:" + tmpFrame);
			}
			((VideoMultiFrame)frame).addFrame((IVideoFrame)tmpFrame);
		}
		else if(frame instanceof IAudioFrame) {
			AudioMultiFrame multiFrame = new AudioMultiFrame();
			multiFrame.addFrame((IAudioFrame)frame);
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("try to add non-audioFrame for audioFrame.");
			}
			multiFrame.addFrame((IAudioFrame)tmpFrame);
			frame = multiFrame;
		}
		else if(frame instanceof IVideoFrame) {
			VideoMultiFrame multiFrame = new VideoMultiFrame();
			multiFrame.addFrame((IVideoFrame)frame);
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("try to add non-videoFrame for videoFrame.");
			}
			multiFrame.addFrame((IVideoFrame)tmpFrame);
			frame = multiFrame;
		}
		else {
			throw new Exception("unknown frame is detected.");
		}
	}
	/**
	 * trackId参照
	 * @return
	 */
	public EbmlValue getTrackId() {
		return trackId;
	}
	/**
	 * clusterとのtimestampの差分について参照する
	 * @return
	 */
	protected Bit16 getTimestampDiff() {
		return timestampDiff;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append(" trackId:").append(trackId.get());
		data.append(" timeDiff:").append(timestampDiff.get());
		return data.toString();
	}
}
