package com.ttProject.container.mpegts.type;

import java.nio.ByteBuffer;

import com.ttProject.container.mpegts.ProgramPacket;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * Pat(Program Association Table)
 * 474000100000B00D0001C100000001F0002AB104B2
 * @author taktod
 */
public class Pat extends ProgramPacket {
	private Bit16 programNum = new Bit16();
	private Bit3  reserved   = new Bit3();
	private Bit13 pmtPid     = new Bit13();
	private Bit32 crc32      = new Bit32();
	/**
	 * コンストラクタ
	 * @param syncByte
	 * @param transportErrorIndicator
	 * @param payloadUnitStartIndicator
	 * @param transportPriority
	 * @param pid
	 * @param scramblingControl
	 * @param adaptationFieldExist
	 * @param payloadFieldExist
	 * @param continuityCounter
	 */
	public Pat(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
		super.update();
	}
	/**
	 * デフォルトコンストラクタ
	 */
	public Pat() {
		this(new Bit8(0x47), new Bit1(), new Bit1(1), new Bit1(),
				new Bit13(), new Bit2(), new Bit1(), new Bit1(1),
				new Bit4()
		);
		try {
			super.minimumLoad(new ByteReadChannel(new byte[]{
					0x00, 0x00, (byte)0xB0, 0x0D, 0x00, 0x01, (byte)0xC1, 0x00, 0x00
			}));
		}
		catch(Exception e) {
		}
		programNum.set(1);
		reserved.set(0x07);
		pmtPid.set(0x1000);
		setSectionLength(13);
		super.update();
	}
	public int getPmtPid() {
		return pmtPid.get();
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(programNum, reserved, pmtPid, crc32);
		super.update();
	}

	@Override
	public void load(IReadChannel channel) throws Exception {
		// とりあえず残りのデータ数分skipさせとくか・・・
		BufferUtil.quickDispose(channel, 188 - getSize());
		super.update();
	}

	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		ByteBuffer tmpBuffer = BufferUtil.connect(
				getHeaderBuffer(),
				connector.connect(programNum, reserved, pmtPid)
		);
		int crc32 = calculateCrc(tmpBuffer);
		this.crc32.set(crc32);
		ByteBuffer buffer = ByteBuffer.allocate(188);
		buffer.put(tmpBuffer);
		buffer.putInt(crc32);
		// 埋め
		while(buffer.position() < 188) {
			buffer.put((byte)0xFF);
		}
		buffer.flip();
		super.setData(buffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCrc() {
		return crc32.get();
	}
}
