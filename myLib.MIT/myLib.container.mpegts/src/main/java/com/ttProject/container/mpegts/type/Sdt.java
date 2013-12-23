package com.ttProject.container.mpegts.type;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.ProgramPacket;
import com.ttProject.container.mpegts.field.SdtServiceField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * Sdt(Service Description Table)
 * サンプル
 * 474011100042F0240001C100000001FF 0001FC8013481101054C696261760953657276696365303168C5DB49
 * 47 40 11 10  mpegtsPacketHeader
 * 00 42 F0 24 00 01 C1 00 00  programPacket
 * 00 01 FF  sdt
 * 00 01 FC 80 13  sdtServiceField
 * 48 11  descriptor
 * 01 05 [4C 69 62 61 76] 09 [53 65 72 76 69 63 65 30 31]  ServiceDescriptor
 * 68 C5 DB 49  crc32
 * @see http://en.wikipedia.org/wiki/Service_Description_Table
 * @see http://pda.etsi.org/exchangefolder/en_300468v011301p.pdf
 * ここでは、サービスごとに、descriptorが複数持てるようになっているみたいです。
 * よって、sdtにデータをいれる場合は、serviceIdとdescriptorのデータを指定していれる必要があると思われます。
 * @author taktod
 */
public class Sdt extends ProgramPacket {
	/** ロガー */
	private Logger logger = Logger.getLogger(Sdt.class);
	private Bit16 originalNetworkId = new Bit16();
	private Bit8 reservedFutureUse2 = new Bit8();
	private List<SdtServiceField> serviceFields = new ArrayList<SdtServiceField>();
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
	public Sdt(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
	}
	/**
	 * デフォルトコンストラクタ
	 */
	public Sdt() {
		this(new Bit8(0x47), new Bit1(),
				new Bit1(1), new Bit1(),
				new Bit13(0x11), new Bit2(), new Bit1(),
				new Bit1(1), new Bit4());
		// デフォルトのminimumLoadをここで発動する必要あり
		try {
			super.minimumLoad(new ByteReadChannel(new byte[]{
				0x00, 0x42, (byte)0xF0, 0x24, 0x00, 0x01, (byte)0xC1, 0x00, 0x00,
			}));
			originalNetworkId.set(1);
			reservedFutureUse2.set(0xFF);
		}
		catch(Exception e) {
		}
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(originalNetworkId, reservedFutureUse2);
		// ここからsdtServiceFieldの値を読み込む必要あり
		int size = getSectionLength() - 8;
		while(size > 4) { // 4byte以上ある場合は処理するデータがあると見る(4byteはcrc)
			SdtServiceField ssfield = new SdtServiceField();
			ssfield.load(channel);
			size -= ssfield.getSize();
			logger.info(ssfield);
			serviceFields.add(ssfield);
		}
		loader.load(crc32);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// とりあえず残りのデータ数分skipさせとくか・・・
		BufferUtil.quickDispose(channel, 188 - getSize());
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
