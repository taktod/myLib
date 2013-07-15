package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.field.SdtServiceField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Sdt(Service Description Table)
 * サンプル
 * 474011100042F0240001C100000001FF 0001FC8013481101054C696261760953657276696365303168C5DB49
 * @see http://en.wikipedia.org/wiki/Service_Description_Table
 * @see http://pda.etsi.org/exchangefolder/en_300468v011301p.pdf
 * @author taktod
 */
public class Sdt extends Packet {
	// 内容データ
	private Bit8 tableId; // 0x42固定
	private Bit1 sectionSyntaxIndicator;
	private Bit1 reservedFutureUse1;
	private Bit2 reserved1;
	private short sectionLength; // 12bit // これがこの後に読み込むべきデータ量をしめしている。
	private short transportStreamId; // 16bit
	private Bit2 reserved2;
	private Bit5 versionNumber;
	private Bit1 currentNextIndicator;
	private Bit8 sectionNumber;
	private Bit8 lastSectionNumber;
	private short originalNetworkId; // 16bit
	private Bit8 reservedFutureUse2;
	
	private List<SdtServiceField> serviceFields = new ArrayList<SdtServiceField>();
	
	/**
	 * コンストラクタ
	 */
	public Sdt(ByteBuffer buffer) {
		this(0, buffer);
	}
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Sdt(int position, ByteBuffer buffer) {
		super(position, buffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		IReadChannel channel = new ByteReadChannel(getBuffer());
		// 先頭の部分解析しておく。
		analyzeHeader(channel);
		tableId = new Bit8();
		sectionSyntaxIndicator = new Bit1();
		reservedFutureUse1 = new Bit1();
		reserved1 = new Bit2();
		Bit4 sectionLength_1 = new Bit4();
		Bit8 sectionLength_2 = new Bit8();
		Bit8 transportStreamId_1 = new Bit8();
		Bit8 transportStreamId_2 = new Bit8();
		reserved2 = new Bit2();
		versionNumber = new Bit5();
		currentNextIndicator = new Bit1();
		sectionNumber = new Bit8();
		lastSectionNumber = new Bit8();
		Bit8 originalNetworkId_1 = new Bit8();
		Bit8 originalNetworkId_2 = new Bit8();
		reservedFutureUse2 = new Bit8();
		Bit.bitLoader(channel,
				tableId, sectionSyntaxIndicator, reservedFutureUse1,
				reserved1,
				sectionLength_1, sectionLength_2,
				transportStreamId_1, transportStreamId_2,
				reserved2, versionNumber, currentNextIndicator, sectionNumber,
				lastSectionNumber, originalNetworkId_1, originalNetworkId_2,
				reservedFutureUse2);
		sectionLength = (short)((sectionLength_1.get() << 8) | sectionLength_2.get());
		transportStreamId = (short)((transportStreamId_1.get() << 8) | transportStreamId_2.get());
		originalNetworkId = (short)((originalNetworkId_1.get() << 8) | originalNetworkId_2.get());
		// ループで読み込むべきサイズはsectionLength - 8
		int size = sectionLength - 8;
		while(size > 4) { // まだデータがのこっていたらループで読み込みを実行する。
			SdtServiceField ssfield = new SdtServiceField();
			ssfield.analyze(channel);
			size -= ssfield.getSize();
			serviceFields.add(ssfield);
		}
//		System.out.println(dump2());
		return;
	}
	public String dump2() {
		StringBuilder data = new StringBuilder("sdt:");
		data.append(" ti:").append(Integer.toHexString(tableId.get()));
		data.append(" ssi:").append(sectionSyntaxIndicator);
		data.append(" rfu1:").append(reservedFutureUse1);
		data.append(" r1:").append(reserved1);
		data.append(" sl:").append(Integer.toHexString(sectionLength));
		data.append(" tsi:").append(Integer.toHexString(transportStreamId));
		data.append(" r2:").append(reserved2);
		data.append(" vn:").append(versionNumber);
		data.append(" cni:").append(currentNextIndicator);
		data.append(" sn:").append(sectionNumber);
		data.append(" lsn:").append(lastSectionNumber);
		data.append(" oni:").append(Integer.toHexString(originalNetworkId));
		data.append(" rfu2:").append(reservedFutureUse2);
		return data.toString();
	}
	@Override
	public String toString() {
		return "Sdt: "; // 内容が解析済みなら、そのデータをDumpしておきたいところ
	}
}
