package com.ttProject.container.mkv.type;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.Type;
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
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

/**
 * SimpleBlockタグ
 * データは次のようになっています。
 * A3 44 B4 81 00 00 80 00 00 02 6C ...
 *  A3[SimpleBlockタグ]
 *  44 B4[このTagのサイズデータ]
 * ここまでは読み込み動作実装済み
 *  81[EbmlValue] 動作トラックデータ
 *  00 00[16bit固定]このCluster上でのtimestamp差分量
 *  1000 0000
 *  . keyFrameであるか指定
 *   ... reserved0設定
 *       . 非表示フレームであるか？ 1なら非表示
 *        .. lacing設定(データが複数のフレームの塊の場合にどのようにわかれるかの指定がはいっている*1)
 *          . discardable:なんだろう？
 * *1:h264のnalはフレームの塊ではあるけど、lacingではなくnal構造で分かれるようになっています。
 * @see http://matroska.org/technical/specs/index.html#simpleblock_structure
 * @author taktod
 */
public class SimpleBlock extends MkvBinaryTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SimpleBlock.class);
	private EbmlValue trackId            = new EbmlValue();
	private Bit16     timestampDiff      = new Bit16();
	private Bit1      keyFrameFlag       = new Bit1();
	private Bit3      reserved           = new Bit3();
	private Bit1      invisibleFrameFlag = new Bit1();
	private Bit2      lacing             = new Bit2();
	private Bit1      discardableFlag    = new Bit1();
	private long time = 0;
	private IFrame frame = null;
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SimpleBlock(EbmlValue size) {
		super(Type.SimpleBlock, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(trackId, timestampDiff, keyFrameFlag, reserved, invisibleFrameFlag, lacing, discardableFlag);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.load(channel);
		// データが取得できたので、必要なframeを取得しておきたい。
		// まず時間データについて調整しておく。
		time = getMkvTagReader().getClusterTime() + timestampDiff.get();

		// この部分はanalyzeFrameとして別関数にしておく。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getRemainedSize() {
		return getMkvSize() - (trackId.getBitCount() + 24) / 8;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	/**
	 * フレームを参照する
	 * @return
	 */
	public IFrame getFrame() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame;
	}
	/**
	 * フレームを解析する
	 * @throws Exception
	 */
	private void analyzeFrame() throws Exception {
		// frameデータを調整したい。
		TrackEntry entry = getMkvTagReader().getTrackEntry(trackId.get());
		IReadChannel frameDataChannel = null;
		ContentEncodings encodings = entry.getEncodings();
		// TODO この書き方だと、lacing対策していないので、調整する必要あり。
		if(encodings == null) {
			frameDataChannel = new ByteReadChannel(getMkvData());
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
								throw new Exception("zlib動作は作成できていません。作者に問い合わせてください");
							case Bzlib:
							case Lzo1x:
								throw new Exception("非推奨な圧縮形式でした。:" + compression.getAlgoType());
							case HeaderStripping:
								logger.info("stripでした。");
								logger.info(HexUtil.toHex(BufferUtil.connect(compression.getSettingData(), getMkvData()), 0, 5, true));
								logger.info(HexUtil.toHex(BufferUtil.connect(compression.getSettingData(), getMkvData()), 0, 5, true));
								frameDataChannel = new ByteReadChannel(BufferUtil.connect(compression.getSettingData(), getMkvData()));
								break;
							default:
								throw new Exception("想定外の圧縮algoです");
							}
						}
						else {
							throw new Exception("encodingの保持データが未知なものを発見しました。:" + etag);
						}
					}
				}
				else {
					throw new Exception("contentEncodingsが想定外のデータを保持していました。:" + tag);
				}
			}
		}
		IAnalyzer analyzer = entry.getAnalyzer();
		IFrame analyzedFrame = null;
		do {
			analyzedFrame = analyzer.analyze(frameDataChannel);
			if(analyzedFrame == null) {
				throw new Exception("frameがありませんでした。");
			}
			if(analyzedFrame instanceof NullFrame || !(analyzedFrame instanceof Frame)) {
				continue;
			}
			Frame tmpFrame = (Frame)analyzedFrame;
			tmpFrame.setPts(time);
			tmpFrame.setTimebase(entry.getTimebase());
			addFrame(tmpFrame);
		} while(frameDataChannel.size() != frameDataChannel.position());
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
	public void addFrame(IFrame tmpFrame) throws Exception {
		if(tmpFrame == null) {
			return;
		}
		if(frame == null) {
			frame = tmpFrame;
		}
		else if(frame instanceof AudioMultiFrame) {
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("audioFrameの追加バッファとしてaudioFrame以外をうけとりました。");
			}
			((AudioMultiFrame)frame).addFrame((IAudioFrame)tmpFrame);
		}
		else if(frame instanceof VideoMultiFrame) {
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("videoFrameの追加バッファとしてvideoFrame以外を受け取りました:" + tmpFrame);
			}
			((VideoMultiFrame)frame).addFrame((IVideoFrame)tmpFrame);
		}
		else if(frame instanceof IAudioFrame) {
			AudioMultiFrame multiFrame = new AudioMultiFrame();
			multiFrame.addFrame((IAudioFrame)frame);
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("audioFrameの追加バッファとしてaudioFrame以外を受け取りました");
			}
			multiFrame.addFrame((IAudioFrame)tmpFrame);
			frame = multiFrame;
		}
		else if(frame instanceof IVideoFrame) {
			VideoMultiFrame multiFrame = new VideoMultiFrame();
			multiFrame.addFrame((IVideoFrame)frame);
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("videoFrameの追加バッファとしてvideoFrame以外を受け取りました");
			}
			multiFrame.addFrame((IVideoFrame)tmpFrame);
			frame = multiFrame;
		}
		else {
			throw new Exception("frameのデータに不明なデータがはいりました。");
		}
	}
	public int getTrackId() {
		return trackId.get();
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
