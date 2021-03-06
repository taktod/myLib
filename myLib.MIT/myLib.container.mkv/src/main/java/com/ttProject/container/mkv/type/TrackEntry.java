/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.container.mkv.type.TrackType.Media;
import com.ttProject.container.riff.type.Fmt;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.aac.AacDsiFrameAnalyzer;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrameAnalyzer;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.DataNalAnalyzer;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.H264FrameSelector;
import com.ttProject.frame.h265.H265DataNalAnalyzer;
import com.ttProject.frame.h265.H265FrameSelector;
import com.ttProject.frame.mjpeg.MjpegFrameAnalyzer;
import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
import com.ttProject.frame.theora.TheoraFrameAnalyzer;
import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.frame.vp8.Vp8FrameAnalyzer;
import com.ttProject.frame.vp9.Vp9FrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TrackEntry
 * @author taktod
 */
public class TrackEntry extends MkvMasterTag {
	/** logger */
	private Logger logger = Logger.getLogger(TrackEntry.class);
	private long  timebase; // timebase is settled by others(come from Info)
	private int   lacingFlag = 0; // global lacing type.
	@SuppressWarnings("unused")
	private Media type = null; // type depend on child elements.(frames)

	/** frame analyzer */
	private IAnalyzer analyzer = null;
	/**
	 * constructor
	 * @param size
	 */
	public TrackEntry(EbmlValue size) {
		super(Type.TrackEntry, size);
	}
	/**
	 * constructor
	 */
	public TrackEntry() {
		this(new EbmlValue());
	}
	/**
	 * setup entry
	 * make it easy to use after loading.
	 * @param defaultTimebase (meaning is same as frame and container. 1000 means 1milisec. 44100 means 1/44100 sec)
	 * @return trackId
	 */
	public int setupEntry(long defaultTimebase) throws Exception {
		timebase = defaultTimebase;
		TrackNumber trackNumber = null;
		CodecPrivate codecPrivate = null;
		CodecID codecId = null;
		int width = 0;
		int height = 0;
		int bitDepth = 16;
		int channels = 0;
		int samplingRate = 0;
		for(MkvTag tag : getChildList()) {
			// seems to better to use trackNumber instead of trackId
			if(tag instanceof TrackNumber) {
				trackNumber = (TrackNumber)tag;
			}
			else if(tag instanceof FlagLacing) {
				lacingFlag = (int)((FlagLacing) tag).getValue();
			}
			else if(tag instanceof CodecID) {
				codecId = (CodecID)tag;
			}
			else if(tag instanceof CodecPrivate) {
				codecPrivate = (CodecPrivate)tag;
			}
			else if(tag instanceof Video) {
				for(MkvTag vTag : ((Video) tag).getChildList()) {
					if(vTag instanceof PixelWidth) {
						width = (int)((PixelWidth) vTag).getValue();
					}
					else if(vTag instanceof PixelHeight) {
						height = (int)((PixelHeight) vTag).getValue();
					}
				}
			}
			else if(tag instanceof Audio) {
				for(MkvTag aTag : ((Audio) tag).getChildList()) {
					if(aTag instanceof SamplingFrequency) {
						samplingRate = (int)((SamplingFrequency) aTag).getValue();
					}
					else if(aTag instanceof Channels) {
						channels = (int)((Channels) aTag).getValue();
					}
					else if(aTag instanceof BitDepth) {
						bitDepth = (int)((BitDepth) aTag).getValue();
					}
				}
			}
			else if(tag instanceof TrackType) {
				type = ((TrackType)tag).getType();
			}
		}
		if(trackNumber == null) {
			throw new Exception("trackNumber is undefined.");
		}
		switch(codecId.getMkvCodecType()) {
		case A_AAC:
			analyzer = new AacDsiFrameAnalyzer();
			analyzer.setPrivateData(new ByteReadChannel(codecPrivate.getMkvData()));
			break;
		case A_MPEG_L3:
			analyzer = new Mp3FrameAnalyzer();
			break;
		case A_VORBIS:
			analyzer = new VorbisFrameAnalyzer();
			((VorbisFrameAnalyzer)analyzer).setPrivateData(new ByteReadChannel(codecPrivate.getMkvData()));
			break;
		case A_MS_ACM:
			Fmt fmt = new Fmt();
			ByteBuffer privateBuffer = codecPrivate.getMkvData();
			ByteBuffer buffer = ByteBuffer.allocate(privateBuffer.remaining() + 4);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.putInt(privateBuffer.remaining());
			buffer.put(privateBuffer);
			buffer.flip();
			IReadChannel channel = new ByteReadChannel(buffer);
			fmt.minimumLoad(channel);
			fmt.load(channel);
			codecId.setCodecType(fmt.getCodecType());
			// check the information of fmt.
			switch(codecId.getCodecType()) {
			case ADPCM_IMA_WAV:
				analyzer = new AdpcmImaWavFrameAnalyzer();
				break;
			default:
				throw new RuntimeException(codecId.getCodecType() + " is under construct for mkv.");
			}
			break;
		case V_MPEG4_ISO_AVC:
			DataNalAnalyzer dataNalAnalyzer = new DataNalAnalyzer();
			// for h264, load sps and pps from CodecPrivate information.
			ConfigData configData = new ConfigData();
			configData.setSelector((H264FrameSelector)dataNalAnalyzer.getSelector());
			configData.analyzeData(new ByteReadChannel(codecPrivate.getMkvData()));
			dataNalAnalyzer.setConfigData(configData);
			analyzer = dataNalAnalyzer;
			break;
		case V_MPEG_ISO_HEVC:
			logger.info("h265 is not check yet.");
			analyzer = new H265DataNalAnalyzer();
			com.ttProject.frame.h265.ConfigData h265ConfigData = new com.ttProject.frame.h265.ConfigData();
			h265ConfigData.setSelector((H265FrameSelector)((H265DataNalAnalyzer)analyzer).getSelector());
			h265ConfigData.analyze(new ByteReadChannel(codecPrivate.getMkvData()));
			break;
		case V_THEORA:
			analyzer = new TheoraFrameAnalyzer();
			((TheoraFrameAnalyzer)analyzer).setPrivateData(new ByteReadChannel(codecPrivate.getMkvData()));
			break;
		case V_MJPEG:
			analyzer = new MjpegFrameAnalyzer();
			break;
		case V_VP8:
			analyzer = new Vp8FrameAnalyzer();
			break;
		case V_VP9:
			logger.info("vp9 is not check yet.");
			analyzer = new Vp9FrameAnalyzer();
			break;
		default:
			throw new Exception("unexpected codec.");
		}
		if(analyzer instanceof AudioAnalyzer) {
			AudioSelector selector = ((AudioAnalyzer)analyzer).getSelector();
			selector.setBit(bitDepth);
			selector.setChannel(channels);
			selector.setSampleRate(samplingRate);
		}
		else if(analyzer instanceof VideoAnalyzer) {
			VideoSelector selector = ((VideoAnalyzer)analyzer).getSelector();
			selector.setWidth(width);
			selector.setHeight(height);
		}
		return (int)trackNumber.getValue();
	}
	/**
	 * ref analyzer
	 * @return
	 */
	public IAnalyzer getAnalyzer() {
		return analyzer;
	}
	/**
	 * ref timebase
	 */
	@Override
	public long getTimebase() {
		return timebase;
	}
	/**
	 * ref the lacing flag.
	 * @return
	 */
	public int getLacingFlag() {
		return lacingFlag;
	}
	/**
	 * ref encoding
	 * @return
	 */
	public ContentEncodings getEncodings() {
		for(MkvTag tag : getChildList()) {
			if(tag instanceof ContentEncodings) {
				return (ContentEncodings)tag;
			}
		}
		return null;
	}
	/**
	 * ref codecType
	 * @return
	 * @throws Exception
	 */
	public CodecType getCodecType() throws Exception {
		for(MkvTag tag : getChildList()) {
			if(tag instanceof CodecID) {
				return ((CodecID) tag).getCodecType();
			}
		}
		throw new Exception("CodecID is undefined.");
	}
	/**
	 * set the codecType.
	 * @param codecType
	 */
	public void setCodecType(CodecType codecType) throws Exception {
		CodecID codecId = new CodecID();
		codecId.setCodecType(codecType);
		addChild(codecId);
	}
	/**
	 * setup frame.
	 * @param trackId
	 * @param frame
	 * @throws Exception
	 */
	public void setupFrame(int trackId, IFrame frame, long defaultTimebase) throws Exception {
		timebase = defaultTimebase;
		TrackNumber trackNumber = new TrackNumber();
		trackNumber.setValue(trackId);
		addChild(trackNumber);
		TrackUID trackUID = new TrackUID();
		trackUID.setValue(trackId);
		addChild(trackUID);
		FlagLacing flagLacing = new FlagLacing();
		flagLacing.setValue(0);
		addChild(flagLacing);
		Language language = new Language();
		language.setValue("und");
		addChild(language);
		if(frame instanceof IAudioFrame) {
			IAudioFrame aFrame = (IAudioFrame)frame;
			TrackType trackType = new TrackType();
			trackType.setType(Media.Audio);
			addChild(trackType);
			
			Audio audio = new Audio();
			audio.setup(aFrame);
			addChild(audio);
			switch(aFrame.getCodecType()) {
			case AAC:
				{
					AacFrame aacFrame = (AacFrame)aFrame;
					CodecPrivate codecPrivate = new CodecPrivate();
					codecPrivate.setValue(aacFrame.getPrivateData());
					addChild(codecPrivate);
				}
				break;
			case VORBIS:
				{
					VorbisFrame vorbisFrame = (VorbisFrame)aFrame;
					CodecPrivate codecPrivate = new CodecPrivate();
					codecPrivate.setValue(vorbisFrame.getPrivateData());
					addChild(codecPrivate);
				}
				break;
			case SPEEX:
			case OPUS:
				logger.error(aFrame.getCodecType() + " is under construction.");
				break;
			default:
				break;
			}
		}
		else if(frame instanceof IVideoFrame) {
			IVideoFrame vFrame = (IVideoFrame)frame;
			TrackType trackType = new TrackType();
			trackType.setType(Media.Video);
			addChild(trackType);
			
			DefaultDuration defaultDuration = new DefaultDuration();
			defaultDuration.setValue(1000000L);
			addChild(defaultDuration);
			
			Video video = new Video();
			video.setup(vFrame);
			addChild(video);
			// h264 and h265 we need to makeup CodecPrivate.
			switch(vFrame.getCodecType()) {
			case H264:
				{
					H264Frame h264Frame = (H264Frame) vFrame;
					// h264のcodecPrivateを作る必要あり
					com.ttProject.frame.h264.ConfigData configData = new com.ttProject.frame.h264.ConfigData();
					CodecPrivate codecPrivate = new CodecPrivate();
					codecPrivate.setValue(configData.makeConfigData(h264Frame.getSps(), h264Frame.getPps()));
					addChild(codecPrivate);
				}
				break;
			case H265:
				logger.error("h265 is under construction.");
				break;
			default:
				break;
			}
		}
	}
}
