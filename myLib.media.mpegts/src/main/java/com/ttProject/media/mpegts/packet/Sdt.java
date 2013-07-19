package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.ProgramPacket;
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
public class Sdt extends ProgramPacket {
	/** 巡回データカウンター */
	private static byte counter = 0;
	// 内容データ
	private short originalNetworkId; // 16bit
	private Bit8 reservedFutureUse2;
	private List<SdtServiceField> serviceFields = new ArrayList<SdtServiceField>();
	
	/**
	 * コンストラクタ
	 */
	public Sdt() {
		super(0);
	}
	/**
	 * コンストラクタ
	 */
	public Sdt(ByteBuffer buffer) throws Exception {
		this(0, buffer);
	}
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Sdt(int position, ByteBuffer buffer) throws Exception  {
		super(position);
		analyze(new ByteReadChannel(buffer));
	}
	@Override
	public void setupDefault() throws Exception {
		// counterの部分が一定ではないはずなので、これではだめになる。
		// payloadだけフラグをたてておく。
		analyzeHeader(new ByteReadChannel(new byte[]{
			0x47, 0x40, 0x11, 0x10, // payloadだけフラグたててある。
			0x00, 0x42, (byte)0xF0, 0x24, 0x00, 0x01, (byte)0xC1, 0x00, 0x00
		}), counter ++);
		if(counter > 0x0F) {
			counter = 0;
		}
		originalNetworkId = 1;
		reservedFutureUse2 = new Bit8(0xFF);
		// serviceFieldの中身はあとで決める必要あり
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		// 先頭の部分解析しておく。
		analyzeHeader(ch, counter ++);
		if(counter > 0x0F) {
			counter = 0;
		}
		Bit8 originalNetworkId_1 = new Bit8();
		Bit8 originalNetworkId_2 = new Bit8();
		reservedFutureUse2 = new Bit8();
		Bit.bitLoader(ch, originalNetworkId_1, originalNetworkId_2,
				reservedFutureUse2);
		originalNetworkId = (short)((originalNetworkId_1.get() << 8) | originalNetworkId_2.get());
		// ループで読み込むべきサイズはsectionLength - 8
		int size = getSectionLength() - 8;
		while(size > 4) { // まだデータがのこっていたらループで読み込みを実行する。
			SdtServiceField ssfield = new SdtServiceField();
			ssfield.analyze(ch);
			size -= ssfield.getSize();
			serviceFields.add(ssfield);
		}
		System.out.println(dump2());
		return;
	}
	public String dump2() {
		StringBuilder data = new StringBuilder("sdt:");
		data.append(" oni:").append(Integer.toHexString(originalNetworkId));
		data.append(" rfu2:").append(reservedFutureUse2);
		return data.toString();
	}
	@Override
	public String toString() {
		return "Sdt: "; // 内容が解析済みなら、そのデータをDumpしておきたいところ
	}
}
