package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.CodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.H264FrameSelector;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * 映像用のtag
 * @author taktod
 * このtagがvideoAnalyzerを保持しているのは、flvTagはあとからflame化ができるため。
 * mpegtsもIAnalyzerを保持しておいて、あとからframe化できるようになってますね。
 * 勘違いしてました。
 */
public class VideoTag extends FlvTag {
	/** ロガー */
	private Logger logger = Logger.getLogger(VideoTag.class);
	private Bit4 frameType = new Bit4();
	private Bit4 codecId   = new Bit4();
	
	private Bit4  horizontalAdjustment = null; // vp6のみ
	private Bit4  verticalAdjustment   = null; // vp6のみ
	private Bit32 offsetToAlpha        = null; // vp6aのみ
	private Bit8  packetType           = null; // avcのみ
	private Bit24 dts                  = null; // avcのみ

	private ByteBuffer frameBuffer = null; // フレームデータ
	private ByteBuffer alphaData   = null; // vp6a用のalphaデータ
	private IVideoFrame   frame = null; // 動作対象フレーム
	private VideoAnalyzer frameAnalyzer = null;
	/**
	 * コンストラクタ
	 * @param tagType
	 */
	public VideoTag(Bit8 tagType) {
		super(tagType);
	}
	/**
	 * デフォルトコンストラクタ
	 */
	public VideoTag() {
		this(new Bit8(0x09));
	}
	/**
	 * フレーム解析Analyzer設定
	 * @param analyzer
	 */
	public void setFrameAnalyzer(VideoAnalyzer analyzer) {
		this.frameAnalyzer = analyzer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// こちら側のloadが実行された場合は、読み込みデータをとりにいく。
		if(codecId != null) {
			BitLoader loader = null;
			switch(getCodec()) {
			case H264:
				channel.position(getPosition() + 16);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 16 - 4);
				if(packetType.get() == 0) {
					// mshの場合はconfigDataを構築しておく。
					ConfigData configData = new ConfigData();
					configData.setSelector((H264FrameSelector)frameAnalyzer.getSelector());
					frame = configData.getNalsFrame(new ByteReadChannel(frameBuffer));
				}
				break;
			case ON2VP6:
				horizontalAdjustment = new Bit4();
				verticalAdjustment = new Bit4();
				loader = new BitLoader(channel);
				loader.load(horizontalAdjustment, verticalAdjustment);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 13 - 4);
				break;
			case ON2VP6_ALPHA:
				offsetToAlpha = new Bit32();
				loader = new BitLoader(channel);
				loader.load(offsetToAlpha);
				int offset = offsetToAlpha.get();
				frameBuffer = BufferUtil.safeRead(channel, offset);
				alphaData = BufferUtil.safeRead(channel, getSize() - 16 - 4 - offset);
				break;
			default:
				channel.position(getPosition() + 12);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 12 - 4);
				break;
			}
		}
		// prevTagSizeを確認しておく。
		if(getPrevTagSize() != BufferUtil.safeRead(channel, 4).getInt()) {
			throw new Exception("終端タグのデータ量がおかしいです。");
		}
	}
	/**
	 * コーデック参照
	 * @return
	 */
	public CodecType getCodec() {
		return CodecType.getVideoCodecType(codecId.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		if(getSize() == 15) {
			// データの内部サイズが0の場合もありえます。
			logger.warn("内部データのないタグができました。");
			return;
		}
		// コーデック情報等を取得する必要あり
		BitLoader loader = new BitLoader(channel);
		loader.load(frameType, codecId);
		if(getCodec() == CodecType.H264) {
			// h264用の特殊データも読み込んでおく。
			packetType = new Bit8();
			dts = new Bit24();
			loader.load(packetType, dts);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null && frame == null) {
			throw new Exception("データ更新の要求がありましたが、内容データが決定していません。");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer videoInfoBuffer = connector.connect(
				frameType, codecId, 
				horizontalAdjustment, verticalAdjustment, // vp6
				offsetToAlpha, // vp6a
				packetType, dts // avc
		);
		ByteBuffer frameBuffer = getFrameBuffer();
		ByteBuffer tailBuffer = getTailBuffer();
		setData(BufferUtil.connect(
				startBuffer,
				videoInfoBuffer,
				frameBuffer,
				alphaData,
				tailBuffer
		));
	}
	/**
	 * frame用buffer参照
	 * @return
	 */
	private ByteBuffer getFrameBuffer() {
		if(frameBuffer == null) {
			// frameから復元する必要あり。
		}
		return frameBuffer.duplicate();
	}
	/**
	 * フレーム解析実行
	 * @throws Exception
	 */
	private void analyzeFrame() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameデータが読み込まれていません");
		}
		if(getCodec() == CodecType.H264 && packetType.get() != 1) {
			// h264でpacketTypeが0:mshや2:endOfSequenceの場合はframeがとれない。
			return;
		}
		ByteBuffer buffer = frameBuffer;
		if(frameAnalyzer == null) {
			throw new Exception("frameの解析プログラムが設定されていません。");
		}
		IReadChannel channel = new ByteReadChannel(buffer);
		// video側はコンテナからwidthとheightの情報取得できないので、放置しておく。
		do {
			VideoFrame videoFrame = (VideoFrame)frameAnalyzer.analyze(channel);
			videoFrame.setPts(getPts());
			videoFrame.setTimebase(getTimebase());
			if(dts != null) {
				videoFrame.setDts(dts.get());
			}
			if(frame != null) {
				if(!(frame instanceof VideoMultiFrame)) {
					VideoMultiFrame multiFrame = new VideoMultiFrame();
					multiFrame.addFrame(frame);
					frame = multiFrame;
				}
				((VideoMultiFrame)frame).addFrame((IVideoFrame)videoFrame);
			}
			else {
				frame = (IVideoFrame)videoFrame;
			}
		} while(channel.size() != channel.position());
	}
	/**
	 * フレーム参照
	 * @return
	 * @throws Exception
	 */
	public IVideoFrame getFrame() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame;
	}
	/**
	 * 横幅
	 * @return
	 * @throws Exception
	 */
	public int getWidth() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame.getWidth();
	}
	/**
	 * 高さ
	 * @return
	 * @throws Exception
	 */
	public int getHeight() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame.getHeight();
	}
	/**
	 * frameを設定する
	 * @param frame
	 */
	public void setFrame(IVideoFrame frame) throws Exception {
		// frameから各情報を復元しないとだめ
		// 時間情報
		// size情報
		// streamId(0固定)
		// tagデータ(frameType, codecId)
		// (vp6,vp6a,h264の場合の特殊データ)
		// frameデータ実体
		// tail size
	}
	/**
	 * mshであるかどうか応答
	 * @return
	 */
	public boolean isSequenceHeader() {
		return getCodec() == CodecType.H264 && packetType.get() == 0;
	}
	/**
	 * キーフレームであるかの判定
	 * @return
	 */
	public boolean isKeyFrame() {
		return frameType.get() == 1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("VideoTag:");
		data.append(" timestamp:").append(getPts());
		data.append(" codec:").append(getCodec());
		try {
			int width = getWidth();
			int height = getHeight();
			data.append(" size:").append(width).append("x").append(height);
		}
		catch(Exception e) {
		}
		return data.toString();
	}
}
