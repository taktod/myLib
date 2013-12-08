package com.ttProject.frame.aac.type;

import com.ttProject.frame.aac.AacFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit2;
import com.ttProject.unit.extra.Bit3;
import com.ttProject.unit.extra.Bit4;
import com.ttProject.unit.extra.BitN.Bit11;
import com.ttProject.unit.extra.BitN.Bit12;
import com.ttProject.unit.extra.BitN.Bit13;

/**
 * @see http://wiki.multimedia.cx/index.php?title=ADTS
 * profile, the MPEG-4 Audio Object Type minus 1
 * 
 * @author taktod
 *
 */
public class Frame extends AacFrame {
	private Bit12 syncBit = new Bit12();
	private Bit1 id;
	private Bit2 layer;
	private Bit1 protectionAbsent;
	private Bit2 profile; // -1した値がはいっているみたい。
	private Bit4 samplingFrequenceIndex;
	private Bit1 privateBit;
	private Bit3 channelConfiguration;
	private Bit1 originalFlg;
	private Bit1 home;
	private Bit1 copyrightIdentificationBit;
	private Bit1 copyrightIdentificationStart;
	private Bit13 frameSize;
	private Bit11 adtsBufferFullness;
	private Bit2 noRawDataBlocksInFrame;
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub

	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// TODO Auto-generated method stub

	}
	@Override
	protected void requestUpdate() throws Exception {
		// TODO Auto-generated method stub

	}
}
