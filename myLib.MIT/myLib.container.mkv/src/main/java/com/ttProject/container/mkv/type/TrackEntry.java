package com.ttProject.container.mkv.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.util.HexUtil;

/**
 * TrackEntryタグ
 * ここからデータを拾えるようにだけ調整しておきたいね。
 * @author taktod
 */
public class TrackEntry extends MkvMasterTag {
	/** ロガー */
	private Logger logger = Logger.getLogger(TrackEntry.class);
	private long timebase;
	private int lacingFlag = 0;

	private CodecID   codecId   = null;
	private PixelWidth  pixelWidth  = null;
	private PixelHeight pixelHeight = null;
	private Channels          channels          = null;
	private SamplingFrequency samplingFrequency = null;
	private BitDepth          bitDepth          = null;
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
		TrackUID trackUid = null;
		CodecPrivate codecPrivate = null;
		for(MkvTag tag : getChildList()) {
			if(tag instanceof TrackUID) {
				trackUid = (TrackUID)tag;
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
		}
		if(trackUid == null) {
			throw new Exception("trackUidが見つかりませんでした。");
		}
		switch(codecId.getCodecType()) {
		case A_AAC:
			break;
		case A_MPEG_L3:
			break;
		case A_VORBIS:
			break;
		case V_MPEG4_ISO_AVC:
			break;
		case V_VP8:
			break;
		default:
			throw new Exception("想定外のcodecでした。");
		}
		logger.info(codecId.getCodecType());
		if(codecPrivate != null) {
			logger.info(HexUtil.toHex(codecPrivate.getMkvData(), true));
		}
		return (int)trackUid.getValue();
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
}
