package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.CodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.util.BufferUtil;

/**
 * 音声用のtag
 * @author taktod
 *
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
	private IUnit frame = null;
	public AudioTag(Bit8 tagType) {
		super(tagType);
	}
	public AudioTag() {
		this(new Bit8(0x08));
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
}
