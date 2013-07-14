package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.media.mpegts.Packet;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Sdt(Service Description Table)
 * サンプル
 * 474011100042F0240001C100000001FF 0001FC8013481101054C696261760953657276696365303168C5DB49
 * @see http://en.wikipedia.org/wiki/MPEG_transport_stream
 * @author taktod
 */
public class Sdt extends Packet {
	/** ロガー */
	private static final Logger logger = Logger.getLogger(Sdt.class);
	// PESデータなので、0x474011に先頭がなります。
	private final int pid = 0x0011; // IDは固定
	// 内容データ
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
		// 直接ここにくるはず。
		// 先頭の3バイトはすでに読み込み済みになっているはず。
		ByteBuffer buffer = getBuffer();
		// すでに3バイトすすんでいるところから解析するので、2バイトスキップさせる必要あり。
		buffer.position(5);
		// どうやらセクションシンタクス指示の部分が、データによって違うみたいです。
		// よってとりあえず、バラバラにすすめたいとおもいます。
		// テーブルシグネチャの確認
		if(buffer.get() != 0x42) {
			throw new Exception("テーブルシグネチャが一致しませんでした。");
		}
		int data = buffer.getShort() & 0xFFFF;
		// セクションシンタックスインディケーター(1ビット1固定) 1?
		// reserved to future use(1ビット:不明) 1?
		// reserved(2ビット:不明) 11?
		if((data & 0x8000) == 0) {
			throw new Exception("セクションシンタクスインディケーターのビットがおかしいです。");
		}
		int size = data & 0x0FFF; // データ内部のサイズ(CRC32の部分まで含めた全体の長さになります。)

		// 以降のデータはsizeに収まります。
		int pid = buffer.getShort() & 0xFFFF; // transportStreamId(わからなければ0x0001固定でよさそう。)
		size -= 2;
		data = buffer.get() & 0xFF;
		size -= 1;
		// reserved 2bit 11固定っぽい。
		// version number 5bit 0で良さそう
		int versionNumber = (data & 0x3E) >>> 1;
		// currentNextIndicator 1bit 1たてておく
		byte currentNextOrder = (byte)(data & 0x01);
		// sectionNumber 8bit 0x00でいいみたい
		int sectionNumber = buffer.get() & 0xFF;
		size -= 1;
		// lastSectionNumber 8bit 0x00
		int lastSectionNumber = buffer.get() & 0xFF;
		size -= 1;

		// originalNetworkId(16bit) 0x0001
		int originalNetworkId = buffer.getShort() & 0xFFFF;
		size -= 2;
		// reserved for future use(8bit) 0xFFうめておく。
		buffer.get();
		size -= 1;
		
		// 中身のループ
		// 4以上データがある場合(終端がcrcであるため)
		while(size > 4) {
			// serviceId(16bit)
			int serviceId = buffer.getShort() & 0xFFFF;
			size -= 2;
			// reservedFutureUse(6bit) EIT_ScheduleFlag(1bit) EITPresentFollowingFlag(1bit)
			data = buffer.get() & 0xFF;
			size -= 1;
			byte EITScheduleFlag = (byte)((data & 0x02) >>> 1);
			byte EITPresentFollowingFlag = (byte)(data & 0x01);
			// runningStatus(3bit) freeCAMode(1bit) descriptorsLoopLength(12bit)
			data = buffer.getShort() & 0xFFFF;
			size -= 2;
			byte runningStatus = (byte)((data & 0xE000) >>> 13);
			byte freeCAMode = (byte)((data & 0x1000) >>> 12); // 1がはいっていたらスクランブルはいっている
			int innerDescSize = (data & 0x0FFF);
			size -= innerDescSize;
			while(innerDescSize > 0) {
				// serviceDescriptorの中身
				// tag(8bit)0x48?
				byte Tag = buffer.get();
				innerDescSize -= 1;
				
				// descriptorLength(8bit)
				int descriptorLength = buffer.get();
				innerDescSize -= 1;
				innerDescSize -= descriptorLength;
				// serviceType(8bit)
				byte type = buffer.get();
				// serviceProviderNameLength(8bit)
				int providerNameLength = buffer.get();
				// データchar(Nbit)
				byte[] str = new byte[providerNameLength];
				buffer.get(str);

				System.out.println(new String(str).intern());
				// serviceNameLength(8bit)
				int serviceNameLength = buffer.get();
				// データchar(Nbit)
				str = new byte[serviceNameLength];
				buffer.get(str);
				System.out.println(new String(str).intern());
				System.out.println(innerDescSize);
			}
		}
	}
	@Override
	public String toString() {
		return "Sdt: "; // 内容が解析済みなら、そのデータをDumpしておきたいところ
	}
}
