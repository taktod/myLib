package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.ProgramPacket;
import com.ttProject.media.mpegts.descriptor.Descriptor;
import com.ttProject.media.mpegts.descriptor.ServiceDescriptor;
import com.ttProject.media.mpegts.field.SdtServiceField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Sdt(Service Description Table)
 * サンプル
 * 474011100042F0240001C100000001FF 0001FC8013481101054C696261760953657276696365303168C5DB49
 * @see http://en.wikipedia.org/wiki/Service_Description_Table
 * @see http://pda.etsi.org/exchangefolder/en_300468v011301p.pdf
 * ここでは、サービスごとに、descriptorが複数持てるようになっているみたいです。
 * よって、sdtにデータをいれる場合は、serviceIdとdescriptorのデータを指定していれる必要があると思われます。
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
	public Sdt() throws Exception {
		super(0);
		setupDefault();
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
		}));
		originalNetworkId = 1;
		reservedFutureUse2 = new Bit8(0xFF);
		// serviceFieldの中身はあとで決める必要あり
	}
	/**
	 * 基本と鳴るDefaultProviderデータを書き込む
	 * @param provider
	 * @param name
	 */
	public void writeDefaultProvider(String provider, String name) {
		// serviceFieldがあるか確認、１番のサービスフィールドにデータがなければ・・・
		SdtServiceField targetField = null;
		for(SdtServiceField ssfield : serviceFields) {
			// serviceIdが1のデータをみつける。
			if(ssfield.getServiceId() == 1) {
				targetField = ssfield;
				break;
			}
		}
		if(targetField == null) {
			targetField = new SdtServiceField();
			serviceFields.add(targetField);
		}
		// 内部で保持している、descriptorsに同じproviderのdescriptorがあるか確認して、ある場合は上書き、ない場合は追加する。
		ServiceDescriptor serviceDescriptor = null;
		for(Descriptor descriptor : targetField.getDescriptors()) {
			if(descriptor instanceof ServiceDescriptor) {
				serviceDescriptor = (ServiceDescriptor)descriptor;
				break;
			}
		}
		if(serviceDescriptor == null) {
			// データがセットされていない場合は上書きしてやる
			serviceDescriptor = new ServiceDescriptor();
		}
		serviceDescriptor.setName(provider, name);
		targetField.addDescriptor(serviceDescriptor);
		// 変更した場合にsetSectionLengthを変更する必要あり。
		short length = 0;
		// 5 sectionLength以降とprogramPacketデータ
		length += 5;
		// 3 sdtのデータ
		length += 3;
		// x sdtServiceFieldの長さ
		for(SdtServiceField ssfield : serviceFields) {
			length += ssfield.getSize();
		}
		// 4 crc32の長さ
		length += 4;
		// sdtServiceFieldの長さを調べます。
		setSectionLength(length);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		// 先頭の部分解析しておく。
		analyzeHeader(ch);
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
		return;
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		setContinuityCounter(counter ++);
		return super.getBuffer();
	}
	/**
	 * 巡回cc値を設定して動作するgetBuffer
	 * @param counter
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer getBuffer(int counter) throws Exception {
		setContinuityCounter(counter);
		return super.getBuffer();
	}
	@Override
	public List<Bit> getBits() {
		List<Bit> list = super.getBits();
		list.add(new Bit8(originalNetworkId >>> 8));
		list.add(new Bit8(originalNetworkId));
		list.add(reservedFutureUse2);
		for(SdtServiceField ssfield : serviceFields) {
			list.addAll(ssfield.getBits());
		}
		return list;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("Sdt:");
		data.append("\n").append(super.toString());
		data.append(" oni:").append(Integer.toHexString(originalNetworkId));
		data.append(" rfu2:").append(reservedFutureUse2);
		for(SdtServiceField ssfield : serviceFields) {
			data.append("\n");
			data.append(ssfield);
		}
		return data.toString();
	}
}
