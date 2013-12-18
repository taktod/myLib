package com.ttProject.container.flv;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.MetaTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.aac.AacDsiFrameAnalyzer;
import com.ttProject.frame.adpcmswf.AdpcmswfFrameAnalyzer;
import com.ttProject.frame.flv1.Flv1FrameAnalyzer;
import com.ttProject.frame.h264.DataNalAnalyzer;
import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
import com.ttProject.frame.nellymoser.NellymoserFrameAnalyzer;
import com.ttProject.frame.speex.SpeexFrameAnalyzer;
import com.ttProject.frame.speex.SpeexFrameSelector;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.frame.vp6.Vp6FrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * flvのtagを解析して取り出すselector
 * @author taktod
 */
public class FlvTagSelector implements ISelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FlvTagSelector.class);
	private VideoAnalyzer videoFrameAnalyzer = null;
	private AudioAnalyzer audioFrameAnalyzer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			// もうおわり
			return null;
		}
		// 始めの8bitを見ればなんのデータか一応わかる。
		Bit8 firstByte = new Bit8();
		BitLoader loader = new BitLoader(channel);
		loader.load(firstByte);
		IUnit unit;
		if(firstByte.get() == 'F') {
			// headerデータ
			Bit16 restSignature = new Bit16();
			loader.load(restSignature);
			Bit24 signature = new Bit24(firstByte.get() << 16 | restSignature.get());
			unit = new FlvHeaderTag(signature);
			unit.minimumLoad(channel);
			return unit;
		}
		else {
			// その他のtagであると思われる。
			switch(firstByte.get()) {
			case 0x12:
				MetaTag metaTag = new MetaTag(firstByte);
				metaTag.minimumLoad(channel);
				return metaTag;
			case 0x09:
				VideoTag videoTag = new VideoTag(firstByte);
				videoTag.minimumLoad(channel);
				switch(videoTag.getCodec()) {
				case JPEG:
					// flvのjpegフォーマットはもう利用されていないらしい。
					break;
				case FLV1:
					if(videoFrameAnalyzer == null || !(videoFrameAnalyzer instanceof Flv1FrameAnalyzer)) {
						videoFrameAnalyzer = new Flv1FrameAnalyzer();
					}
					break;
				case SCREEN:
					break;
				case ON2VP6:
					if(videoFrameAnalyzer == null || !(videoFrameAnalyzer instanceof Vp6FrameAnalyzer)) {
						videoFrameAnalyzer = new Vp6FrameAnalyzer();
					}
					break;
				case ON2VP6_ALPHA:
					if(videoFrameAnalyzer == null || !(videoFrameAnalyzer instanceof Vp6FrameAnalyzer)) {
						videoFrameAnalyzer = new Vp6FrameAnalyzer();
					}
					break;
				case SCREEN_V2:
					break;
				case H264:
					// h264のときのための特殊な読み込みanalyzerをつくっておくべきっぽいですね。
					if(videoFrameAnalyzer == null || !(videoFrameAnalyzer instanceof DataNalAnalyzer)) {
						videoFrameAnalyzer = new DataNalAnalyzer(); // これはminimumLoadの時点でいれなきゃだめなのか・・・そりゃむりだ
					}
					break;
				default:
					break;
				}
				videoTag.setFrameAnalyzer(videoFrameAnalyzer);
				return videoTag;
			case 0x08:
				AudioTag audioTag = new AudioTag(firstByte);
				audioTag.minimumLoad(channel);
				switch(audioTag.getCodec()) {
				case PCM:
					break;
				case ADPCM:
					if(audioFrameAnalyzer == null || !(audioFrameAnalyzer instanceof AdpcmswfFrameAnalyzer)) {
						audioFrameAnalyzer = new AdpcmswfFrameAnalyzer();
					}
					break;
				case MP3:
					if(audioFrameAnalyzer == null || !(audioFrameAnalyzer instanceof Mp3FrameAnalyzer)) {
						audioFrameAnalyzer = new Mp3FrameAnalyzer();
					}
					break;
				case LPCM:
					break;
				case NELLY_16:
				case NELLY_8:
				case NELLY:
					if(audioFrameAnalyzer == null || !(audioFrameAnalyzer instanceof NellymoserFrameAnalyzer)) {
						audioFrameAnalyzer = new NellymoserFrameAnalyzer();
					}
					break;
				case G711_A:
					break;
				case G711_U:
					break;
				case RESERVED:
					break;
				case AAC:
					if(audioFrameAnalyzer == null || !(audioFrameAnalyzer instanceof AacDsiFrameAnalyzer)) {
						audioFrameAnalyzer = new AacDsiFrameAnalyzer();
					}
					break;
				case SPEEX:
					if(audioFrameAnalyzer == null || !(audioFrameAnalyzer instanceof SpeexFrameAnalyzer)) {
						audioFrameAnalyzer = new SpeexFrameAnalyzer();
						SpeexFrameSelector speexFrameSelector = (SpeexFrameSelector)audioFrameAnalyzer.getSelector();
						// headerFrameとcommentFrameを設定しておく。
						HeaderFrame headerFrame = new HeaderFrame();
						headerFrame.fillWithFlvDefault();
						speexFrameSelector.setHeaderFrame(headerFrame);
						speexFrameSelector.setCommentFrame(new CommentFrame());
					}
					break;
				case MP3_8:
					if(audioFrameAnalyzer == null || !(audioFrameAnalyzer instanceof Mp3FrameAnalyzer)) {
						audioFrameAnalyzer = new Mp3FrameAnalyzer();
					}
					break;
				case DEVICE_SPECIFIC:
					break;
				default:
					break;
				}
				audioTag.setFrameAnalyzer(audioFrameAnalyzer);
				return audioTag;
			default:
				throw new Exception("想定外のtagです。");
			}
		}
	}
}
