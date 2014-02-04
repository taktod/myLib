package com.ttProject.container.mpegts.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.ProgramPacket;
import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * Pmt
 * marioの
 * 47500010
 * 0002B0170001C10000
 * E100F000
 * 1BE100F000(h264)
 * 0FE101F000(aac)
 * 2F44B99B(CRC32)
 * 
 * rtypeDelta aacのみになったやつ
 * 47500010
 * 0002B0120001C10000
 * E100F000
 * 0FE100F000(aac)
 * B69BC0D9(CRC32)
 * 
 * 
 * vlcが出力したデータ
 * 474042309500FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
 * [      ]ここまでは普通
 *         []adaptationfieldで埋めてある。
 * 0002B01E0001E30000(programPacketで共通化している部分)
 *   []tableId 2
 *      [ ]sectionLength
 *         [  ]programNumber 1
 *             [] version numberが0x11になってる。
 *                   [  ] lastSectionNumberが0ではない
 * E044F000
 * [  ] pcrPidが0x44
 *     [  ]ふつう
 * 81E044F00C 050441432D330A0400000000(1トラック分しかないじゃんw)
 * 81ED7715(crc32)
 * 
 * 0002B01E0001E30000E044F00081E044F00C050441432D330A040000000081ED7715
 * 0002B01E0001E30000E044F00081E044F00C050441432D330A040000000081ED7715
 * 0002B01E0001E30000E044F00081E044F00C050441432D330A040000000081ED7715
 * 
 * 途中から長さがかわってた。始めは1Bのデータ(h264のデータが追加されてますね。)
 * 0002B0290001E50000E045F00081E044F00C050441432D330A04000000001BE045F0060A040000000097DACB3E
 * 0002B0290001E50000
 * E045F000
 * 81E044F00C   05 04 41432D33 0A 04 00000000
 * [1B]E045F006 0A 04 00000000
 * 97DACB3E
 * @author taktod
 */
public class Pmt extends ProgramPacket {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Pmt.class);
	private Bit3  reserved1         = null;
	private Bit13 pcrPid            = null;
	private Bit4  reserved2         = null;
	private Bit12 programInfoLength = null;
	private List<PmtElementaryField> fields = new ArrayList<PmtElementaryField>();
	private Bit32 crc32 = new Bit32();
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
	public Pmt(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
		super.update();
	}
	/**
	 * コンストラクタ
	 * @param pmtPid
	 */
	public Pmt(int pmtPid) {
		this(new Bit8(0x47), new Bit1(), new Bit1(1), new Bit1(),
				new Bit13(pmtPid), new Bit2(), new Bit1(), new Bit1(1),
				new Bit4()
		);
		try {
			super.minimumLoad(new ByteReadChannel(new byte[]{
					0x00, 0x02, (byte)0xB0, 0x0D, 0x00, 0x01, (byte)0xC1, 0x00, 0x00
			}));
		}
		catch(Exception e) {
		}
		reserved1         = new Bit3(0x07);
		pcrPid            = new Bit13(0x0100);
		reserved2         = new Bit4(0x0F);
		programInfoLength = new Bit12();
		super.update();
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(crc32);
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(isLoaded()) {
			return;
		}
		IReadChannel holdChannel = new ByteReadChannel(getBuffer());
		super.load(holdChannel);
		BitLoader loader = new BitLoader(holdChannel);
		reserved1         = new Bit3();
		pcrPid            = new Bit13();
		reserved2         = new Bit4();
		programInfoLength = new Bit12();
		loader.load(reserved1, pcrPid, reserved2, programInfoLength);
		int size = getSectionLength() - 5 - 4 - 4;
		while(size > 0) {
			PmtElementaryField elementaryField = new PmtElementaryField();
			elementaryField.load(holdChannel);
			size -= elementaryField.getSize();
			fields.add(elementaryField);
		}
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		connector.feed(reserved1, pcrPid, reserved2, programInfoLength);
		for(PmtElementaryField elementaryField : fields) {
			connector.feed(elementaryField.getBits());
		}
		// headerと結合する
		ByteBuffer tmpBuffer = BufferUtil.connect(
				getHeaderBuffer(),
				connector.connect()
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
	public int getPcrPid() {
		return pcrPid.get();
	}
	public void setPcrPid(int pid) {
		pcrPid.set(pid);
	}
	public boolean isPesPid(int pid) {
		for(PmtElementaryField field : fields) {
			if(field.getPid() == pid) {
				return true;
			}
		}
		return false;
	}
	public void addNewField(PmtElementaryField field) {
		if(!fields.contains(field)) {
			fields.add(field);
			short length = 0;
			length += 5;
			length += 4;
			for(PmtElementaryField elementaryField : fields) {
				length += (short) elementaryField.getSize();
			}
			length += 4;
			setSectionLength(length);
		}
	}
	public List<PmtElementaryField> getFields() {
		return new ArrayList<PmtElementaryField>(fields);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCrc() {
		return crc32.get();
	}
}
