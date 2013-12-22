package com.ttProject.container.mpegts;

import com.ttProject.container.Container;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * mpegtsのpacket
 * @author taktod
 */
public abstract class MpegtsPacket extends Container {
	private Bit8 syncByte;
	private Bit1 transportErrorIndicator;
	private Bit1 payloadUnitStartIndicator;
	private Bit1 transportPriority;
	private Bit13 pid;
	private Bit2 scramblingControl;
	private Bit1 adaptationFieldExist;
	private Bit1 payloadFieldExist;
	private Bit4 continuityCounter;
}
