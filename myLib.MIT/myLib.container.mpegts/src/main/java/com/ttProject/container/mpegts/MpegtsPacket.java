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
	private final Bit8 syncByte;
	private final Bit1 transportErrorIndicator;
	private final Bit1 payloadUnitStartIndicator;
	private final Bit1 transportPriority;
	private final Bit13 pid;
	private final Bit2 scramblingControl;
	private final Bit1 adaptationFieldExist;
	private final Bit1 payloadFieldExist;
	private final Bit4 continuityCounter;
	public MpegtsPacket(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		this.syncByte = syncByte;
		this.transportErrorIndicator = transportErrorIndicator;
		this.payloadUnitStartIndicator = payloadUnitStartIndicator;
		this.transportPriority = transportPriority;
		this.pid = pid;
		this.scramblingControl = scramblingControl;
		this.adaptationFieldExist = adaptationFieldExist;
		this.payloadFieldExist = payloadFieldExist;
		this.continuityCounter = continuityCounter;
	}
	protected boolean isPayloadUnitStart() {
		return payloadUnitStartIndicator.get() == 1;
	}
}
