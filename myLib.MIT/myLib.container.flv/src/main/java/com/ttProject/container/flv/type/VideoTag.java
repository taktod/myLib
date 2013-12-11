package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.CodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * 映像用のtag
 * @author taktod
 */
public class VideoTag extends FlvTag {
	/** ロガー */
	private Logger logger = Logger.getLogger(VideoTag.class);
	private Bit4 frameType = null;
	private Bit4 codecId = null;
	private Bit8 packetType = null; // avcのみ
	private Bit24 dts = null; // avcのみ
	private ByteBuffer frameBuffer = null; // フレームデータ
	private IVideoFrame frame = null; // 動作対象フレーム
	private IAnalyzer frameAnalyzer = null;
	public VideoTag(Bit8 tagType) {
		super(tagType);
	}
	public VideoTag() {
		this(new Bit8(0x09));
	}
	public void setFrameAnalyzer(IAnalyzer analyzer) {
		this.frameAnalyzer = analyzer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// こちら側のloadが実行された場合は、読み込みデータをとりにいく。
		if(codecId != null) {
			switch(getCodec()) {
			case H264:
				channel.position(getPosition() + 16);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 16 - 4);
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
		frameType = new Bit4();
		codecId = new Bit4();
		loader.load(frameType, codecId);
		if(getCodec() == CodecType.H264) {
			// h264用の特殊データも読み込んでおく。
			packetType = new Bit8();
			dts = new Bit24();
			loader.load(packetType, dts);
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null && frame == null) {
			throw new Exception("データ更新の要求がありましたが、内容データが決定していません。");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer videoInfoBuffer = connector.connect(
				frameType, codecId, packetType, dts
		);
		ByteBuffer frameBuffer = getFrameBuffer();
		ByteBuffer tailBuffer = getTailBuffer();
		setData(BufferUtil.connect(
				startBuffer,
				videoInfoBuffer,
				frameBuffer,
				tailBuffer
		));
	}
	private ByteBuffer getFrameBuffer() {
		if(frameBuffer == null) {
			// frameから復元する必要あり。
		}
		return frameBuffer.duplicate();
	}
	public void analyzeFrame() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameデータが読み込まれていません");
		}
		ByteBuffer buffer = frameBuffer;
		switch(getCodec()) {
		case JPEG:
			frameAnalyzer = null;
			break;
		case FLV1:
			break;
		case SCREEN:
			frameAnalyzer = null;
			break;
		case ON2VP6:
			// vp6の場合は、先頭のデータを終端にもってくる必要あり。
			ByteBuffer frameBuffer = buffer.duplicate();
			buffer = ByteBuffer.allocate(frameBuffer.remaining());
			byte firstByte = frameBuffer.get();
			buffer.put(frameBuffer);
			buffer.put(firstByte);
			buffer.flip();
			break;
		case ON2VP6_ALPHA:
			frameAnalyzer = null;
			break;
		case SCREEN_V2:
			frameAnalyzer = null;
			break;
		case H264:
			frameAnalyzer = null;
			break;
		default:
			break;
		}
		if(frameAnalyzer == null) {
			throw new Exception("frameの解析プログラムが設定されていません。");
		}
		frame = (IVideoFrame)frameAnalyzer.analyze(new ByteReadChannel(buffer));
		logger.info("frame:" + frame);
	}
	public int getWidth() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame.getWidth();
	}
	public int getHeight() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame.getHeight();
	}
}
