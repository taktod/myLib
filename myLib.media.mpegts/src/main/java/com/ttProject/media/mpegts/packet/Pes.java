package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit7;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ElementaryStreamPacket
 * (packetizedElementaryStream Packet)
 * 
 * 47 [abcd dddd] [dddd dddd] [eefg hhhh]
 * a:error indicator
 * b:payload unit start indicator
 * c:transport priority indicator
 * e:scranblingControl
 * f:has adaptation field
 * g:has payload data
 * h:continuty count
 * 
 * adaptationFieldをもっている場合
 * 次の1バイトがadaptation fieldの長さっぽい。
 * flagsとかがはいっているけど、とりあえずいらないので、捨てればよさそう。
 * 
 * payloadDataがはいっているものが、メディアデータとして必要なもの。
 * payLoadUnitStartIndicatorのフラグが立っている場合
 *  h.264の場合は、先頭の4バイトが0x000001E0になっている。
 *  次の3バイトにpacketLengthとmarkerBitがはいっている。(とりあえず無視してる)
 *  次の2ビットがPTSフラグとDTSフラグ(両方ないと、処理できないとする。)
 *  とりあえず、PTSは必須、DTSはなくてもなんとかなるっぽい。
 * 47 41 00 30 07 50 00 00 6F 51 7E 00 00 00 01 E0 06 01 80 C0 0A 31 00 07 D8 61 11 00 07 A9 75
 * 
 * @see http://en.wikipedia.org/wiki/Packetized_elementary_stream
 * @see http://en.wikipedia.org/wiki/Elementary_stream
 * 
 * @author taktod
 */
public class Pes extends Packet {
	/** 巡回データカウンター */
	private static byte counter = 0;
	private int prefix; // 3バイト 0x000001固定
	private Bit8 streamId; // audioなら0xC0 - 0xDF videoなら0xE0 - 0xEF通常は0xC0もしくは0xE0(トラックが１つずつしかないため)
	private short pesPacketLength; // 2バイト
	private Bit2 markerBits; // 10固定
	private Bit2 scramblingControl; // 00
	private Bit1 priority; // 0
	private Bit1 dataAlignmentIndicator; // 0
	private Bit1 copyright; // 0
	private Bit1 originFlg; // 0:original 1:copy

	private Bit2 ptsDtsIndicator; // ここ・・・wikiによると11だとboth、1だとPTSのみと書いてあるけど10の間違いではないですかね。
	private Bit1 escrFlag; // 0
	private Bit1 esRateFlag; // 0
	private Bit1 DSMTrickModeFlag; // 0
	private Bit1 additionalCopyInfoFlag; // 0
	private Bit1 CRCFlag; // 0
	private Bit1 extensionFlag; // 0

	private Bit8 PESHeaederLength; // 10byte?

	// @see http://dvd.sourceforge.net/dvdinfo/pes-hdr.html
	// PTSDTSFlagがついている場合
	// pts only
	// 0010PTS1 PTS..... .......1 PTS.... .......1
	// or
	// pts & dts
	// 0011PTS1 PTS..... .......1 PTS.... .......1
	// 0001DTS1 DTS..... .......1 DTS.... .......1
	
	// note:
	// 両方ある場合は、dtsがtimestamp、dtsとptsの差分がcompositionTimeになるみたいです。
	// ptsしかない場合はptsがtimestamp、compositionTimeは0になります。
	// 5byte
	private Bit4 ptsSignature;
	private Bit3 pts1;
	private Bit1 ptsFlag1;
	private Bit7 pts2;
	private Bit8 pts3;
	private Bit1 ptsFlag2;
	private Bit7 pts4;
	private Bit8 pts5;
	private Bit1 ptsFlag3;
	private long pts;

	// 5byte
	private Bit4 dtsSignature;
	private Bit3 dts1;
	private Bit1 dtsFlag1;
	private Bit7 dts2;
	private Bit8 dts3;
	private Bit1 dtsFlag2;
	private Bit7 dts4;
	private Bit8 dts5;
	private Bit1 dtsFlag3;
	private long dts;

	// 以下のデータは面倒なので、とりあえず未実装にしときます。
	// ESCR(elementaryStreamClockReference)
	// 00ES C1ES CR.. .1ES CR.. .1ES CRex t..1

	// ESRate
	// 1Esr ate. .... .... .... ...1

	// ここから先はh.264のnal構造やaacのadtsデータとかが続く。
	// 最終のパケットで余白が必要な場合adaptationFieldが挿入されてflagがすべてoffなデータが作られるみたい。
	// この場合内容データはすべて0xFFになっている模様。

	/** このエレメントがPcrであるか(時間同期用のパケットであるかどうか) */
	private final boolean pcrFlg;
	/** このエレメントのコーデック情報 */
	private final CodecType codec;
	public Pes(CodecType codec, boolean pcrFlg, short pid) throws Exception {
		super(0);
		this.codec = codec;
		this.pcrFlg = pcrFlg;
		setupDefault(pid); // デフォルトを設定しておく。
	}
	/**
	 * コンストラクタ
	 * @param buffer
	 * @param codec
	 * @param pcrFlg
	 */
	public Pes(ByteBuffer buffer, CodecType codec, boolean pcrFlg) throws Exception {
		this(0, buffer, codec, pcrFlg);
	}
	/**
	 * コンストラクタ
	 * @param position
	 * @param buffer
	 * @param codec
	 * @param pcrFlg
	 */
	public Pes(int position, ByteBuffer buffer, CodecType codec, boolean pcrFlg) throws Exception {
		super(position);
		this.codec = codec;
		this.pcrFlg = pcrFlg;
		analyze(new ByteReadChannel(buffer));
	}
	/**
	 * pcrパケットであるか確認
	 * @return
	 */
	public boolean isPcr() {
		return pcrFlg;
	}
	/**
	 * codec情報を取得
	 * @return
	 */
	public CodecType getCodec() {
		return codec;
	}
	public void setupDefault(short pid) throws Exception {
		// 初期化します。
		byte b1 = (byte)(0x40 | (pid >>> 8));
		byte b2 = (byte)(pid & 0xFF);
		// 47 41 00 30 07 50 00 00 6F 51 7E 00 00 00 01 E0 06 01 80 C0 0A 31 00 07 D8 61 11 00 07 A9 75
		// adaptation fieldも追加しときたいけど、adaptationFieldのデータにpcrはいっていて、このデータがパケット依存なので、やるとしたら
		analyzeHeader(new ByteReadChannel(new byte[]{
				0x47, b1, b2, 0x30, 0x07, 0x50, 0x00, 0x00, 0x00, 0x00, 0x7E, 0x00
		}), counter ++);
		// adaptationFieldの内容はあとでなんとかしておく。
		prefix = 0x000001;
		// コーデック情報をベースに動作をきめていく。
		// 複数トラックがある場合は、incrementする必要があるわけだが・・・まぁおいとく。
		if(codec == CodecType.AUDIO_AAC) {
			streamId = new Bit8(0xC0);
		}
		else if(codec == CodecType.VIDEO_H264) {
			streamId = new Bit8(0xE0);
		}
		// packetLengthはmpegtsのパケットを超えたデータ量全体になる。
		// あとで決める項目だと思う。
		pesPacketLength = 0x0000;
		markerBits = new Bit2(2);
		scramblingControl = new Bit2(0);
		priority = new Bit1(0);
		dataAlignmentIndicator = new Bit1(0);
		copyright = new Bit1(0);
		originFlg = new Bit1(0); // originalをなのっておく。

		// ここなにをいれればいいかちょっとわからない。
		ptsDtsIndicator = new Bit2(2);
		escrFlag = new Bit1(0);
		esRateFlag = new Bit1(0);
		DSMTrickModeFlag = new Bit1(0);
		additionalCopyInfoFlag = new Bit1(0);
		CRCFlag = new Bit1(0);
		extensionFlag = new Bit1(0);
		
	}
	@Override
	public void setupDefault() throws Exception {
		
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		analyzeHeader(ch, counter ++);
		if(counter > 0x0F) {
			counter = 0;
		}
		if(!isPayloadUnitStart()) {
			return;
		}
		// 中身確認
		Bit8 prefix_1 = new Bit8();
		Bit8 prefix_2 = new Bit8();
		Bit8 prefix_3 = new Bit8();
		streamId = new Bit8();
		Bit8 pesPacketLength_1 = new Bit8();
		Bit8 pesPacketLength_2 = new Bit8();
		markerBits = new Bit2();
		scramblingControl = new Bit2();
		priority = new Bit1();
		dataAlignmentIndicator = new Bit1();
		copyright = new Bit1();
		originFlg = new Bit1();
		ptsDtsIndicator = new Bit2();
		escrFlag = new Bit1();
		esRateFlag = new Bit1();
		DSMTrickModeFlag = new Bit1();
		additionalCopyInfoFlag = new Bit1();
		CRCFlag = new Bit1();
		extensionFlag = new Bit1();
		PESHeaederLength = new Bit8();
		Bit.bitLoader(ch, prefix_1, prefix_2, prefix_3, streamId,
				pesPacketLength_1, pesPacketLength_2, markerBits, scramblingControl,
				priority, dataAlignmentIndicator, copyright, originFlg,
				ptsDtsIndicator, escrFlag, esRateFlag, DSMTrickModeFlag, additionalCopyInfoFlag,
				CRCFlag, extensionFlag, PESHeaederLength);
		prefix = (prefix_1.get() << 16) | (prefix_2.get() << 8) | prefix_3.get();
		pesPacketLength = (short)((pesPacketLength_1.get() << 8) | pesPacketLength_2.get());

		int length = PESHeaederLength.get();
		switch(ptsDtsIndicator.get()) {
		case 0x03:
			{
				System.out.println("ptsDts");
				// pts
				ptsSignature = new Bit4();
				pts1 = new Bit3();
				ptsFlag1 = new Bit1();
				pts2 = new Bit7();
				pts3 = new Bit8();
				ptsFlag2 = new Bit1();
				pts4 = new Bit7();
				pts5 = new Bit8();
				ptsFlag3 = new Bit1();
				// dts
				dtsSignature = new Bit4();
				dts1 = new Bit3();
				dtsFlag1 = new Bit1();
				dts2 = new Bit7();
				dts3 = new Bit8();
				dtsFlag2 = new Bit1();
				dts4 = new Bit7();
				dts5 = new Bit8();
				dtsFlag3 = new Bit1();
				Bit.bitLoader(ch,
						ptsSignature, pts1, ptsFlag1,
						pts2, pts3, ptsFlag2,
						pts4, pts5, ptsFlag3,
						dtsSignature, dts1, dtsFlag1,
						dts2, dts3, dtsFlag2,
						dts4, dts5, dtsFlag3);
				if(ptsSignature.get() != 0x03 || dtsSignature.get() != 0x01) {
					throw new Exception("ptsもしくはdtsのsignatureがおかしいです。pts:" + ptsSignature + " dts:" + dtsSignature);
				}
				if(ptsFlag1.get() != 0x01 || ptsFlag2.get() != 0x01 || ptsFlag3.get() != 0x01
				|| dtsFlag1.get() != 0x01 || dtsFlag2.get() != 0x01 || dtsFlag3.get() != 0x01) {
					throw new Exception("中途flagがおかしいです。");
				}
				pts = (long)(((pts1.get() & 0xFFL) << 30) | (pts2.get() << 23) | (pts3.get() << 15) | (pts4.get() << 8) | pts5.get());
				dts = (long)(((dts1.get() & 0xFFL) << 30) | (dts2.get() << 23) | (dts3.get() << 15) | (dts4.get() << 8) | dts5.get());
				length -= 10;
			}
			break;
		case 0x02:
			{
				System.out.println("pts");
				// pts
				ptsSignature = new Bit4();
				pts1 = new Bit3();
				ptsFlag1 = new Bit1();
				pts2 = new Bit7();
				pts3 = new Bit8();
				ptsFlag2 = new Bit1();
				pts4 = new Bit7();
				pts5 = new Bit8();
				ptsFlag3 = new Bit1();
				Bit.bitLoader(ch,
						ptsSignature, pts1, ptsFlag1,
						pts2, pts3, ptsFlag2,
						pts4, pts5, ptsFlag3);
				if(ptsSignature.get() != 0x02) {
					throw new Exception("ptsのsigunatureがおかしいです。pts:" + ptsSignature);
				}
				if(ptsFlag1.get() != 0x01 || ptsFlag2.get() != 0x01 || ptsFlag3.get() != 0x01) {
					throw new Exception("中途flagがおかしいです。");
				}
				pts = (long)(((pts1.get() & 0xFFL) << 30) | (pts2.get() << 23) | (pts3.get() << 15) | (pts4.get() << 8) | pts5.get());
				length -= 5;
			}
			break;
		case 0x00:
			break;
		default:
			throw new Exception("ptsDtsIndicatorが不正です。");
		}
		if(escrFlag.get() != 0x00) {
			throw new Exception("escrFlagの解析は未実装です。");
		}
		if(esRateFlag.get() != 0x00) {
			throw new Exception("esRateFlagの解析は未実装です。");
		}
		if(DSMTrickModeFlag.get() != 0x00) {
			throw new Exception("DSMTrickModeFlagの解析は未実装です。");
		}
		if(additionalCopyInfoFlag.get() != 0x00) {
			throw new Exception("additionalCopyInfoFlagの解析は未実装です。");
		}
		if(CRCFlag.get() != 0x00) {
			throw new Exception("CRCFlagの解析は未実装です。");
		}
		if(extensionFlag.get() != 0x00) {
			throw new Exception("extensionFlagの解析は未実装です。");
		}
		if(length != 0) {
			throw new Exception("読み込みできていないデータがあるみたいです。");
		}
//		System.out.println(dump2());
		System.out.println(dump3());
	}
	public String dump2() {
		StringBuilder data = new StringBuilder("Pes:");
		data.append(" prefix:").append(prefix);
		data.append(" si:").append(Integer.toHexString(streamId.get()));
		data.append(" ppl:").append(Integer.toHexString(pesPacketLength));
		data.append(" mb:").append(markerBits);
		data.append(" sc:").append(scramblingControl);
		data.append(" p:").append(priority);
		data.append(" dai:").append(dataAlignmentIndicator);
		data.append(" c:").append(copyright);
		data.append(" of:").append(originFlg);
		data.append(" pdi:").append(ptsDtsIndicator);
		data.append(" ef:").append(escrFlag);
		data.append(" erf:").append(esRateFlag);
		data.append(" dtmf:").append(DSMTrickModeFlag);
		data.append(" acif:").append(additionalCopyInfoFlag);
		data.append(" cf:").append(CRCFlag);
		data.append(" ef:").append(extensionFlag);
		data.append(" phl:").append(Integer.toHexString(PESHeaederLength.get()));
		return data.toString();
	}
	@Override
	public ByteBuffer getBuffer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public String dump3() {
		StringBuilder data = new StringBuilder();
		if(ptsSignature != null) {
			data.append(" pts:").append(Long.toHexString(pts)).append("(").append(pts / 90000f).append(")");
		}
		if(dtsSignature != null) {
			data.append(" dts:").append(Long.toHexString(dts)).append("(").append(dts / 90000f).append(")");
		}
		return data.toString();
	}
	public String dump4() {
		StringBuilder data = new StringBuilder();
		return data.toString();
	}
	@Override
	public String toString() {
		StringBuilder dump = new StringBuilder();
		dump.append("Pes:");
		dump.append(" codec:").append(codec);
		dump.append(" pcr:").append(pcrFlg);
		return dump.toString();
	}
}
