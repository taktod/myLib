package com.ttProject.container.mpegts;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.Container;
import com.ttProject.container.mpegts.field.AdaptationField;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * mpegtsのpacket
 * @author taktod
 */
public abstract class MpegtsPacket extends Container {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MpegtsPacket.class);
	private final Bit8 syncByte;
	private final Bit1 transportErrorIndicator;
	private final Bit1 payloadUnitStartIndicator;
	private final Bit1 transportPriority;
	private final Bit13 pid;
	private final Bit2 scramblingControl;
	private final Bit1 adaptationFieldExist;
	private final Bit1 payloadFieldExist;
	private final Bit4 continuityCounter;
	private AdaptationField adaptationField = new AdaptationField();
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
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		if(adaptationFieldExist.get() != 0x00) {
			adaptationField.load(channel);
		}
	}
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		if(adaptationFieldExist.get() == 1) {
			return BufferUtil.connect(
				connector.connect(
						syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority,
						pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter),
				connector.connect(adaptationField.getBits())
			);
		}
		else {
			return connector.connect(
					syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority,
					pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
		}
	}
	protected boolean isPayloadUnitStart() {
		return payloadUnitStartIndicator.get() == 1;
	}
	protected void setPayloadUnitStart(int flag) {
		payloadUnitStartIndicator.set(flag);
	}
	public void setContinuityCounter(int counter) {
		continuityCounter.set(counter);
		super.update();
	}
	public int getContinuityCounter() {
		return continuityCounter.get();
	}
	protected void setAdaptationFieldExist(int flag) {
		adaptationFieldExist.set(flag);
	}
	protected boolean isAdaptationFieldExist() {
		return adaptationFieldExist.get() == 1;
	}
	protected AdaptationField getAdaptationField() {
		return adaptationField;
	}
	public int getPid() {
		return pid.get();
	}
}
