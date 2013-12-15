package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.CodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.aac.AacDsiFrameAnalyzer;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * 音声用のtag
 * @author taktod
 * nellymoserの16や8の場合はsampleRate 0 bitCount 1 channels 0になるっぽいです。
 * speexは16khzのはずだけど、sampleRate 1 bitCount 1 channels 0になるっぽいです。
 */
public class AudioTag extends FlvTag {
	/** ロガー */
	private Logger logger = Logger.getLogger(AudioTag.class);
	private Bit4 codecId = null;
	private Bit2 sampleRate = null;
	private Bit1 bitCount = null;
	private Bit1 channels = null;
	private Bit8 sequenceHeaderFlag = null;
	private ByteBuffer frameBuffer = null;
	private IAudioFrame frame = null;
	private IAnalyzer frameAnalyzer = null;
/*	public IAudioFrame getFrame() {
		return frame;
	}*/
	public AudioTag(Bit8 tagType) {
		super(tagType);
	}
	public AudioTag() {
		this(new Bit8(0x08));
	}
	public void setFrameAnalyzer(IAnalyzer analyzer) {
		this.frameAnalyzer = analyzer;
	}
	public int getSampleRate() throws Exception {
		if(frame == null) {
			switch(getCodec()) {
			case NELLY_16:
				return 16000;
			case NELLY_8:
			case MP3_8:
				return 8000;
			default:
				switch(sampleRate.get()) {
				case 0:
					return 5512;
				case 1:
					return 11025;
				case 2:
					return 22050;
				case 3:
					return 44100;
				default:
					throw new Exception("想定外の数値がでました。");
				}
			}
		}
		return frame.getSampleRate();
	}
	public int getSampleNum() throws Exception {
		if(frame == null) {
			analyzeFrame(); // 解析させる
		}
		return frame.getSampleNum();
	}
	public int getChannels() {
		if(frame == null) {
			if(channels.get() == 1) {
				return 2;
			}
			else {
				return 1;
			}
		}
		return frame.getChannel();
	}
	public int getBitCount() {
		if(frame == null) {
			if(bitCount.get() == 1) {
				return 16;
			}
			else {
				return 8;
			}
		}
		return frame.getBit();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(codecId != null) {
			switch(getCodec()) {
			case AAC:
				channel.position(getPosition() + 13);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 13 - 4);
				if(sequenceHeaderFlag.get() == 0) {
					if(!(frameAnalyzer instanceof AacDsiFrameAnalyzer)) {
						throw new Exception("frameAnalyzerがaac(dsi)対応ではないみたいです。");
					}
					DecoderSpecificInfo dsi = new DecoderSpecificInfo();
					dsi.minimumLoad(new ByteReadChannel(frameBuffer));
					((AacDsiFrameAnalyzer)frameAnalyzer).getSelector().setDecoderSpecificInfo(dsi);
				}
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
		return CodecType.getAudioCodecType(codecId.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		if(getSize() == 15) {
			logger.warn("内部データのないタグができました");
			return;
		}
		BitLoader loader = new BitLoader(channel);
		codecId = new Bit4();
		sampleRate = new Bit2();
		bitCount = new Bit1();
		channels = new Bit1();
		loader.load(codecId, sampleRate, bitCount, channels);
		if(getCodec() == CodecType.AAC) {
			sequenceHeaderFlag = new Bit8();
			loader.load(sequenceHeaderFlag);
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null && frame == null) {
			throw new Exception("データ更新の要求がありましたが、内容データが決定していません。");
		}
		BitConnector connector = new BitConnector();
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer audioInfoBuffer = connector.connect(
				codecId, sampleRate, bitCount, channels
		);
		ByteBuffer frameBuffer = getFrameBuffer();
		ByteBuffer tailBuffer = getTailBuffer();
		setData(BufferUtil.connect(
				startBuffer,
				audioInfoBuffer,
				frameBuffer,
				tailBuffer
		));
	}
	private ByteBuffer getFrameBuffer() {
		if(frameBuffer == null) {
			// frameから復元する必要あり
		}
		return frameBuffer.duplicate();
	}
	public void analyzeFrame() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameデータが読み込まれていません");
		}
		if(frameAnalyzer == null) {
			throw new Exception("frameの解析プログラムが設定されていません。");
		}
		frame = (IAudioFrame)frameAnalyzer.analyze(new ByteReadChannel(frameBuffer));
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("audioTag:");
		data.append(" timestamp:").append(getPts());
		data.append(" codec:").append(getCodec());
		try {
			int sampleRate = getSampleRate();
			data.append(" sampleRate:").append(sampleRate);
			int sampleNum = getSampleNum();
			data.append(" sampleNum:").append(sampleNum);
		}
		catch(Exception e) {
//			e.printStackTrace();
		}
		return data.toString();
	}
}
