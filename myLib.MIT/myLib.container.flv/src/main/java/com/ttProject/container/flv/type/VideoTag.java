package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.CodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.frame.flv1.type.DisposableInterFrame;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.frame.h264.DataNalAnalyzer;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.H264FrameSelector;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.vp6.Vp6Frame;
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
	private IVideoFrame   frame         = null; // 動作対象フレーム
	private VideoAnalyzer frameAnalyzer = null;
	private boolean frameAppendFlag     = false; // フレームが追加されたことを検知するフラグ
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
					if(frameAnalyzer == null || !(frameAnalyzer instanceof DataNalAnalyzer)) {
						throw new Exception("h264解析用のNalAnalyzerが設定されていません。");
					}
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
		ByteBuffer frameBuffer = null;
		if(frameAppendFlag) {
			// フレームの追加があったとき(frameデータの再構築が必要です。)
			IVideoFrame codecCheckFrame = frame;
			if(frame instanceof VideoMultiFrame) {
				codecCheckFrame = ((VideoMultiFrame) frame).getFrameList().get(0);
			}
//			frameType;
//			codecId;
			int sizeEx = 0;
			if(codecCheckFrame instanceof Flv1Frame) {
				codecId.set(CodecType.getVideoCodecNum(CodecType.FLV1));
				sizeEx = 0;
			}
			else if(codecCheckFrame instanceof Vp6Frame) {
				// vp6aは対応しないことにします。
				horizontalAdjustment = new Bit4();
				verticalAdjustment = new Bit4();
				codecId.set(CodecType.getVideoCodecNum(CodecType.ON2VP6));
				sizeEx = 1;
			}
			else if(codecCheckFrame instanceof H264Frame) {
				codecId.set(CodecType.getVideoCodecNum(CodecType.H264));
				packetType = new Bit8(1);
				dts = new Bit24((int)(1.0D * frame.getDts() / frame.getTimebase() * 1000));
				sizeEx = 4;
			}
			else {
				throw new Exception("未対応なvideoFrameでした:" + frame);
			}
			if(frame instanceof DisposableInterFrame) {
				frameType.set(3);
			}
			else {
				if(frame.isKeyFrame()) {
					frameType.set(1);
				}
				else {
					frameType.set(2);
				}
			}
			frameBuffer = getFrameBuffer();
			// pts timebase sizeの更新が必要
			setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
			setTimebase(1000);
			setSize(11 + 1 + sizeEx + frameBuffer.remaining() + 4);
		}
		else {
			// フレームの追加がなかったとき
			frameBuffer = getFrameBuffer();
		}
		BitConnector connector = new BitConnector();
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer videoInfoBuffer = connector.connect(
				frameType, codecId, 
				horizontalAdjustment, verticalAdjustment, // vp6
				offsetToAlpha, // vp6a
				packetType, dts // avc
		);
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
	private ByteBuffer getFrameBuffer() throws Exception {
		if(frameBuffer == null) {
			// frameから復元する必要あり。
			// この部分注意が必要
			// vp6やflv1はそのまま戻せばよいが
			// h264の場合は、sizeNalにしないとだめ。
			if(CodecType.getVideoCodecType(codecId.get()) == CodecType.H264) {
				// h264のフレームの場合は、調整することがあるので、やらないとだめ。
				ByteBuffer sizeBuffer = null;
				ByteBuffer nalBuffer = null;
				if(frame instanceof VideoMultiFrame) {
					List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
					for(IVideoFrame vFrame : ((VideoMultiFrame) frame).getFrameList()) {
						if(vFrame instanceof H264Frame) {
							nalBuffer = vFrame.getData();
							sizeBuffer = ByteBuffer.allocate(4);
							sizeBuffer.putInt(nalBuffer.remaining());
							sizeBuffer.flip();
							buffers.add(sizeBuffer);
							buffers.add(nalBuffer);
						}
					}
					frameBuffer = BufferUtil.connect(buffers);
				}
				else {
					nalBuffer = frame.getData();
					sizeBuffer = ByteBuffer.allocate(4);
					sizeBuffer.putInt(nalBuffer.remaining());
					sizeBuffer.flip();
					frameBuffer = BufferUtil.connect(sizeBuffer, nalBuffer);
				}
			}
			else {
				if(frame instanceof VideoMultiFrame) {
					throw new Exception("h264以外でマルチフレームの映像は許可されていません。");
				}
				else {
					frameBuffer = frame.getData();
				}
			}
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
	 * frameを追加する
	 * @param frame
	 */
	public void addFrame(IVideoFrame tmpFrame) throws Exception {
		logger.info("フレームの追加が呼ばれました。");
		if(tmpFrame == null) {
			// 追加データがないなら、処理しない
			return;
		}
		if(!(tmpFrame instanceof IVideoFrame)) {
			throw new Exception("videoTagの追加バッファとして、videoFrame以外を受けとりました。");
		}
		frameAppendFlag = true;
		if(frame == null) {
			frame = tmpFrame;
		}
		else if(frame instanceof VideoMultiFrame) {
			((VideoMultiFrame) frame).addFrame(tmpFrame);
		}
		else {
			VideoMultiFrame multiFrame = new VideoMultiFrame();
			multiFrame.addFrame(frame);
			multiFrame.addFrame(tmpFrame);
			frame = multiFrame;
		}
		// frameから各情報を復元しないとだめ
		// 時間情報
		// size情報
		// streamId(0固定)
		// tagデータ(frameType, codecId)
		// (vp6,vp6a,h264の場合の特殊データ)
		// frameデータ実体
		// tail size
		super.update();
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
	 * h264のmediaSequenceHeaderとして初期化します
	 * @param frame
	 * @param sps
	 * @param pps
	 * @throws Exception
	 */
	public void setH264MediaSequenceHeader(H264Frame frame, SequenceParameterSet sps, PictureParameterSet pps) throws Exception {
		codecId.set(CodecType.getVideoCodecNum(CodecType.H264));
		frameType.set(1); // keyFrame指定
		packetType = new Bit8(0);
		dts = new Bit24(0);
		ConfigData configData = new ConfigData();
		frameBuffer = configData.makeConfigData(sps, pps);
		setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
		setTimebase(1000);
		setSize(11 + 1 + 4 + frameBuffer.remaining() + 4);
		super.update();
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
