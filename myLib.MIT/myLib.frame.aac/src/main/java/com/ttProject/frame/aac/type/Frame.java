package com.ttProject.frame.aac.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit11;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.util.BufferUtil;

/**
 * @see http://wiki.multimedia.cx/index.php?title=ADTS
 * profile, the MPEG-4 Audio Object Type minus 1
 * 
 * @author taktod
 * TODO sampleRateとかいれておかないと処理できなくなる。
 */
public class Frame extends AacFrame {
	/** 動作ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	private Bit12 syncBit                      = new Bit12(0x0FFF);
	private Bit1  id                           = new Bit1();
	private Bit2  layer                        = new Bit2();
	private Bit1  protectionAbsent             = new Bit1(1);
	private Bit2  profile                      = new Bit2(); // -1した値がはいっているみたい。
	private Bit4  samplingFrequenceIndex       = new Bit4(4);
	private Bit1  privateBit                   = new Bit1(1);
	private Bit3  channelConfiguration         = new Bit3(2);
	private Bit1  originalFlg                  = new Bit1(1);
	private Bit1  home                         = new Bit1();
	private Bit1  copyrightIdentificationBit   = new Bit1();
	private Bit1  copyrightIdentificationStart = new Bit1();
	private Bit13 frameSize                    = new Bit13(0);
	private Bit11 adtsBufferFullness           = new Bit11(0x7FF);
	private Bit2  noRawDataBlocksInFrame       = new Bit2();
	
	private ByteBuffer buffer = null;
	/** 周波数テーブル */
	private static int [] sampleRateTable = {
		96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000
	};
	/**
	 * dsiの読み込み
	 * @param size
	 * @param dsi
	 * @param channel
	 * @throws Exception
	 */
	public void loadDecoderSpecificInfo(int size, DecoderSpecificInfo dsi, IReadChannel channel) throws Exception {
		frameSize.set(7 + size);
		profile.set(dsi.getObjectType() - 1);
		samplingFrequenceIndex.set(dsi.getFrequencyIndex());
		channelConfiguration.set(dsi.getChannelConfiguration());
		super.setSize(7 + size);
		super.setReadPosition(channel.position());
		updateFlagData();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(syncBit, id, layer, protectionAbsent, profile,
				samplingFrequenceIndex, privateBit, channelConfiguration,
				originalFlg, home, copyrightIdentificationBit,
				copyrightIdentificationStart, frameSize, adtsBufferFullness,
				noRawDataBlocksInFrame);
		super.setSize(frameSize.get());
		super.setReadPosition(channel.position());
		updateFlagData();
	}
	/**
	 * flagDataを更新する
	 */
	private void updateFlagData() {
		super.setSampleNum(1024);
		super.setChannel(channelConfiguration.get());
		super.setSampleRate(sampleRateTable[samplingFrequenceIndex.get()]);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(super.getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - 7);
	}
	/**
	 * bufferを設定します(使ってない？)
	 * @param buffer
	 */
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("データの実体が設定されていません。");
		}
		BitConnector connector = new BitConnector();
		super.setData(BufferUtil.connect(
				connector.connect(syncBit, id, layer, protectionAbsent, profile,
						samplingFrequenceIndex, privateBit, channelConfiguration,
						originalFlg, home, copyrightIdentificationBit,
						copyrightIdentificationStart, frameSize, adtsBufferFullness,
						noRawDataBlocksInFrame),
				buffer));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
}
