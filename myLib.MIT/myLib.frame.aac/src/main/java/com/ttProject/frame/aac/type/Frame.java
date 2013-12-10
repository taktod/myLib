package com.ttProject.frame.aac.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN.Bit11;
import com.ttProject.unit.extra.BitN.Bit12;
import com.ttProject.unit.extra.BitN.Bit13;
import com.ttProject.util.BufferUtil;

/**
 * @see http://wiki.multimedia.cx/index.php?title=ADTS
 * profile, the MPEG-4 Audio Object Type minus 1
 * 
 * @author taktod
 *
 */
public class Frame extends AacFrame {
	private Bit12 syncBit = new Bit12(0x0FFF);
	private Bit1 id = new Bit1();
	private Bit2 layer = new Bit2();
	private Bit1 protectionAbsent = new Bit1(1);
	private Bit2 profile = new Bit2(); // -1した値がはいっているみたい。
	private Bit4 samplingFrequenceIndex = new Bit4(4);
	private Bit1 privateBit = new Bit1(1);
	private Bit3 channelConfiguration = new Bit3(2);
	private Bit1 originalFlg = new Bit1(1);
	private Bit1 home = new Bit1();
	private Bit1 copyrightIdentificationBit = new Bit1();
	private Bit1 copyrightIdentificationStart = new Bit1();
	private Bit13 frameSize = new Bit13(0);
	private Bit11 adtsBufferFullness = new Bit11(0x7FF);
	private Bit2 noRawDataBlocksInFrame = new Bit2();
	private ByteBuffer buffer = null;
	public Frame() {}
	public Frame(int size, DecoderSpecificInfo dsi) {
		frameSize.set(size);
		profile.set(dsi.getObjectType() - 1);
		samplingFrequenceIndex.set(dsi.getFrequencyIndex());
		channelConfiguration.set(dsi.getChannelConfiguration());
		super.setSize(7 + size);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(syncBit, id, layer, protectionAbsent, profile,
				samplingFrequenceIndex, privateBit, channelConfiguration,
				originalFlg, home, copyrightIdentificationBit,
				copyrightIdentificationStart, frameSize, adtsBufferFullness,
				noRawDataBlocksInFrame);
		super.setSize(7 + frameSize.get());
		super.update();
		super.setReadPosition(channel.position());
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(super.getReadPosition());
		buffer = BufferUtil.safeRead(channel, getSize() - 7);
	}
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}
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
}
