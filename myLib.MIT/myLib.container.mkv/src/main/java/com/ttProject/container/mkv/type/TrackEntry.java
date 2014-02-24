package com.ttProject.container.mkv.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.CodecType;
import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.container.mkv.type.TrackType.Media;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.aac.AacDsiFrameAnalyzer;
import com.ttProject.frame.aac.AacDsiFrameSelector;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.DataNalAnalyzer;
import com.ttProject.frame.h264.H264FrameSelector;
import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.frame.vp8.Vp8FrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * TrackEntryタグ
 * ここからデータを拾えるようにだけ調整しておきたいね。
 * TODO static関数でTrackEntryをねつ造できるようにしておきたいところ。
 * @author taktod
 */
public class TrackEntry extends MkvMasterTag {
	/** ロガー */
	private Logger logger = Logger.getLogger(TrackEntry.class);
	private long  timebase;
	private int   lacingFlag = 0;
	private Media type = null;

	private CodecID codecId = null;
	private PixelWidth  pixelWidth  = null;
	private PixelHeight pixelHeight = null;
	private Channels          channels          = null;
	private SamplingFrequency samplingFrequency = null;
	private BitDepth          bitDepth          = null; // このデータはnullなことがあるみたいです。(aacで実際そうなってた。)
	
	// 圧縮や暗号化がある場合のデータ指定
	private ContentEncodings encodings = null;
	/** frame解析用のオブジェクト */
	private IAnalyzer analyzer = null;
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TrackEntry(EbmlValue size) {
		super(Type.TrackEntry, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * load後に、扱いやすいようにデータを設定しておきます。
	 * @param defaultTimebase
	 * @return trackIdを応答します。(uintですが、そこまで大きな数字になることはほぼないと思うのでintegerにまるめます。)
	 */
	public int setupEntry(long defaultTimebase) throws Exception {
		timebase = defaultTimebase;
		TrackNumber trackNumber = null;
		CodecPrivate codecPrivate = null;
		for(MkvTag tag : getChildList()) {
			logger.info(tag);
			// trackUIDをつかわずにtrackNumberをつかった方がいいっぽい。
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
				// videoをループさせる必要あり
				setupVideo((Video)tag);
			}
			else if(tag instanceof Audio) {
				// audioもループさせる必要あり
				setupAudio((Audio)tag);
			}
			else if(tag instanceof TrackType) {
				type = ((TrackType)tag).getType();
			}
			else if(tag instanceof ContentEncodings) {
				encodings = (ContentEncodings)tag;
			}
		}
		if(trackNumber == null) {
			throw new Exception("trackNumberが見つかりませんでした。");
		}
		switch(codecId.getCodecType()) {
		case A_AAC:
			analyzer = new AacDsiFrameAnalyzer();
			DecoderSpecificInfo dsi = new DecoderSpecificInfo();
			dsi.minimumLoad(new ByteReadChannel(codecPrivate.getMkvData()));
			((AacDsiFrameSelector)((AacDsiFrameAnalyzer)analyzer).getSelector()).setDecoderSpecificInfo(dsi);
			break;
		case A_MPEG_L3:
			analyzer = new Mp3FrameAnalyzer();
			break;
		case A_VORBIS:
			logger.info("vorbisは動作がだめだと思います。");
			analyzer = new VorbisFrameAnalyzer();
			// ここでcodecPrivateのデータを先行して解析する必要あり。
			// 02 1E 56
			// サイズ指定要素２つ
			// １つ目は0x1E
			// ２つ目は0x56
			// 残りは３つ目の要素
			// となります。
			// なおxuggleで変換する場合はIStreamCoderにこのcodecPrivateと同じものを渡す必要があるみたいです。
			IReadChannel privateChannel = new ByteReadChannel(codecPrivate.getMkvData());
			BitLoader loader = new BitLoader(privateChannel);
			Bit8 count = new Bit8();
			Bit8 identificationHeaderSize = new Bit8();
			Bit8 commentHeaderSize = new Bit8();
			loader.load(count, identificationHeaderSize, commentHeaderSize);
			if(count.get() != 2) {
				throw new Exception("count数がvorbisに合致していません。");
			}
			analyzer.analyze(new ByteReadChannel(BufferUtil.safeRead(privateChannel, identificationHeaderSize.get())));
			analyzer.analyze(new ByteReadChannel(BufferUtil.safeRead(privateChannel, commentHeaderSize.get())));
			analyzer.analyze(new ByteReadChannel(BufferUtil.safeRead(privateChannel, privateChannel.size() - privateChannel.position())));
			break;
		case V_MPEG4_ISO_AVC:
			analyzer = new DataNalAnalyzer();
			// h264の場合はConfigDataからsps ppsを取り出す必要あり。
			ConfigData configData = new ConfigData();
			configData.setSelector((H264FrameSelector)((DataNalAnalyzer)analyzer).getSelector());
			configData.getNalsFrame(new ByteReadChannel(codecPrivate.getMkvData())); // sps ppsを解析することでH264FrameSelectorにデータがセットされる
			break;
		case V_VP8:
			logger.info("vp8は動作があやしいです。");
			analyzer = new Vp8FrameAnalyzer();
			break;
		default:
			throw new Exception("想定外のcodecでした。");
		}
		if(analyzer instanceof AudioAnalyzer) {
			AudioSelector selector = ((AudioAnalyzer)analyzer).getSelector();
			selector.setBit(getBitDepth());
			selector.setChannel(getChannels());
			selector.setSampleRate((int)getSampleRate());
		}
		else if(analyzer instanceof VideoAnalyzer) {
			VideoSelector selector = ((VideoAnalyzer)analyzer).getSelector();
			selector.setWidth(getWidth());
			selector.setHeight(getHeight());
		}
		return (int)trackNumber.getValue();
	}
	private void setupVideo(Video video) {
		for(MkvTag tag : video.getChildList()) {
			if(tag instanceof PixelWidth) {
				pixelWidth = (PixelWidth)tag;
			}
			else if(tag instanceof PixelHeight) {
				pixelHeight = (PixelHeight)tag;
			}
		}
	}
	private void setupAudio(Audio audio) {
		for(MkvTag tag : audio.getChildList()) {
			if(tag instanceof SamplingFrequency) {
				samplingFrequency = (SamplingFrequency)tag;
			}
			else if(tag instanceof Channels) {
				channels = (Channels)tag;
			}
			else if(tag instanceof BitDepth) {
				bitDepth = (BitDepth)tag;
			}
		}
	}
	/**
	 * analyzer参照
	 * @return
	 */
	public IAnalyzer getAnalyzer() {
		return analyzer;
	}
	public long getTimebase() {
		return timebase;
	}
	public int getLacingFlag() {
		return lacingFlag;
	}
	public CodecType getCodecType() throws Exception {
		return codecId.getCodecType();
	}
	public int getWidth() throws Exception {
		if(type != Media.Video) {
			throw new Exception("データタイプが映像ではありませんでした。");
		}
		return (int)pixelWidth.getValue();
	}
	public int getHeight() throws Exception {
		if(type != Media.Video) {
			throw new Exception("データタイプが映像ではありませんでした。");
		}
		return (int)pixelHeight.getValue();
	}
	public int getChannels() throws Exception {
		if(type != Media.Audio) {
			throw new Exception("データタイプが音声ではありませんでした。");
		}
		return (int)channels.getValue();
	}
	public double getSampleRate() throws Exception {
		if(type != Media.Audio) {
			throw new Exception("データタイプが音声ではありませんでした。");
		}
		return samplingFrequency.getValue();
	}
	public int getBitDepth() throws Exception {
		if(type != Media.Audio) {
			throw new Exception("データタイプが音声ではありませんでした。");
		}
		if(bitDepth == null) {
			return 32;
		}
		return (int)bitDepth.getValue();
	}
	public ContentEncodings getEncodings() {
		return encodings;
	}
}
