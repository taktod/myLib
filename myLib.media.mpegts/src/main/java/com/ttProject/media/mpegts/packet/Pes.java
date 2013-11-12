package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.field.AdaptationField;
import com.ttProject.media.mpegts.field.DtsField;
import com.ttProject.media.mpegts.field.PtsField;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

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
 * getBuffer()を実行すると、前から順にpacketデータが取り出せていって・・・というのがよさそうです。
 * →getBufferで自動的に次のデータとしておくとファイルから読み取った場合とかの処理が詰まってしまうので、変更する必要あり。
 * よって次のようにしたいとおもいます。
 * 連続するデータの場合はnextPesというメソッドを作成して、次のpesデータがある場合は、それを応答する形にしておく。
 * 
 * データの読み込み時の動作を改良する必要がある。
 * 現状では、対象パケットのデータを読み込んでおわりだが、変更したら、読み込みデータから本当のデータ量を取り出す必要がでてくる。
 * 
 * @see http://ameblo.jp/sogoh/entry-10560067493.html
 * 
 * @author taktod
 */
public class Pes extends Packet {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Pes.class);
	/** 巡回データカウンター */
	private static Map<Integer, Integer> counterMap = new HashMap<Integer, Integer>();
	private int prefix; // 3バイト 0x000001固定
	private Bit8 streamId; // audioなら0xC0 - 0xDF videoなら0xE0 - 0xEF通常は0xC0もしくは0xE0(トラックが１つずつしかないため)
	private short pesPacketLength; // 2バイト(この値はpesの以下のデータも含んだデータ長になる必要がある。)
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
	private PtsField pts = null;
	private DtsField dts = null;

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

	/** 保持している生データ */
	// TODO 読み込み処理中にこのデータも作成すべき
	private ByteBuffer rawData;
	/**
	 * コンストラクタ(データ作成用)
	 * @param codec コーデック情報
	 * @param pcrFlg pcrであるかのフラグ
	 * @param randomAccessFlg ランダムにアクセス可能であるかフラグ
	 * @param pid packetIdの値
	 * @param rawData 内部で保持する生データ
	 * @throws Exception
	 */
	public Pes(CodecType codec, boolean pcrFlg, boolean randomAccessFlg, short pid, ByteBuffer rawData) throws Exception {
		this(codec, pcrFlg, randomAccessFlg, pid, rawData, -1, -1);
	}
	/**
	 * コンストラクタ(データ作成用)
	 * @param codec コーデック情報
	 * @param pcrFlg pcrであるかのフラグ
	 * @param randomAccessFlg ランダムにアクセス可能であるかフラグ
	 * @param pid packetIdの値
	 * @param rawData 内部で保持する生データ
	 * @param presentationTimestamp pts
	 * @throws Exception
	 */
	public Pes(CodecType codec, boolean pcrFlg, boolean randomAccessFlg, short pid, ByteBuffer rawData, long presentationTimestamp) throws Exception {
		this(codec, pcrFlg, randomAccessFlg, pid, rawData, presentationTimestamp, -1);
	}
	/**
	 * コンストラクタ(データ作成用)
	 * @param codec コーデック情報
	 * @param pcrFlg pcrであるかフラグ
	 * @param randomAccessFlg ランダムにアクセス可能であるかフラグ
	 * @param pid packetIdの値
	 * @param rawData 内部で保持する生データ
	 * @param presentationTimestamp pts
	 * @param decodeTimestamp dts
	 * @throws Exception
	 */
	public Pes(CodecType codec, boolean pcrFlg, boolean randomAccessFlg, short pid, ByteBuffer rawData, long presentationTimestamp, long decodeTimestamp) throws Exception {
		super(0);
		this.codec = codec;
		this.pcrFlg = pcrFlg;
		setupDefault(pid, randomAccessFlg); // デフォルトを設定しておく。
		// keyFrameもしくは、codecが音声の場合は、randomAccessIndicatorをつけておきたい。
		this.rawData = rawData.duplicate(); // コピーでデータを保持しておく。
		setPesPacketLength((short)(rawData.remaining() + 3));
		if(getPesPacketLength() < 0) {
			throw new Exception("pesPacketのサイズがoverflowしました。データの粒度が大きすぎます。");
		}
		// pcr
		if(pcrFlg) {
			AdaptationField aField = getAdaptationField();
			aField.setPcrBase(presentationTimestamp);
		}
		// pts
		if(presentationTimestamp != -1) {
			PtsField pts = new PtsField();
			pts.setPts(presentationTimestamp);
			setPts(pts);
			PESHeaederLength.set(PESHeaederLength.get() + 5); // pts分データ量が増える
			setPesPacketLength((short)(getPesPacketLength() + 5));
			// dts
			if(decodeTimestamp != -1) {
				ptsDtsIndicator = new Bit2(3);
				DtsField dts = new DtsField();
				dts.setDts(decodeTimestamp);
				setDts(dts);
				PESHeaederLength.set(PESHeaederLength.get() + 5); // dts分データ量が増える
				setPesPacketLength((short)(getPesPacketLength() + 5));
			}
			else {
				ptsDtsIndicator = new Bit2(2);
			}
		}
		checkLength(false);
	}
	/**
	 * コンストラクタ(データ参照用)
	 * @param buffer
	 * @param codec
	 * @param pcrFlg
	 */
	public Pes(ByteBuffer buffer, CodecType codec, boolean pcrFlg) throws Exception {
		this(0, buffer, codec, pcrFlg);
	}
	/**
	 * コンストラクタ(データ参照用)
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
	/**
	 * デフォルト値を生成します。
	 * @param pid
	 * @throws Exception
	 */
	public void setupDefault(short pid, boolean randomAccessFlg) throws Exception {
		// 初期化します。
		byte b1 = (byte)(0x40 | (pid >>> 8)); // 強制的にpayload unit startにしてある。
		byte b2 = (byte)(pid & 0xFF);
		// 47 41 00 30 07 50 00 00 6F 51 7E 00 00 00 01 E0 06 01 80 C0 0A 31 00 07 D8 61 11 00 07 A9 75
		// adaptation fieldも追加しときたいけど、adaptationFieldのデータにpcrはいっていて、このデータがパケット依存なので、やるとしたら
		// デフォルトとして、pcr付きのデータということにしておきます。
		// TODO ここもおかしい。pcrだったら750というわけではない。(50の部分がrandomAccessできて)
		// h264のkeyFrameかaudioFrameの場合はadaptationFieldのrandomAccessIndicatorが必要だし。
		// h264のinnerFrameの場合は、pcrフラグがついててもついてなくてもいいはず。
		if(isPcr()) {
			// pcrがある場合はadaptationField(pcr付きということにしておく)
			analyzeHeader(new ByteReadChannel(new byte[]{
					0x47, b1, b2, 0x30, 0x07, 0x10, 0x00, 0x00, 0x00, 0x00, 0x7E, 0x00
			}));
		}
		else {
			// pcrがない場合はなにもなしとしておく。
			analyzeHeader(new ByteReadChannel(new byte[]{
					0x47, b1, b2, 0x10
			}));
		}
		if(randomAccessFlg) {
			// 強制的にadaptationField持たせる。
			setAdaptationFieldExist(1);
			// randomAccessIndicatorのフラグをたてておく。
			getAdaptationField().setRandomAccessIndicator(1);
		}
		prefix = 0x000001;
		// コーデック情報をベースに動作をきめていく。
		// 複数トラックがある場合は、incrementする必要があるわけだが・・・まぁおいとく。
		if(codec == CodecType.AUDIO_AAC || codec == CodecType.AUDIO_MPEG1) {
			streamId = new Bit8(0xC0);
		}
		else if(codec == CodecType.VIDEO_H264) {
			streamId = new Bit8(0xE0);
		}
		// packetLengthはmpegtsのパケットを超えたデータ量全体になる。
		// このデータがあるため、pesのデータはpesを構築する前に決定している必要がある。
		pesPacketLength = 0x0000;
		markerBits = new Bit2(2);
		scramblingControl = new Bit2(0);
		priority = new Bit1(0);
		dataAlignmentIndicator = new Bit1(0);
		copyright = new Bit1(0);
		originFlg = new Bit1(0); // originalをなのっておく。

		ptsDtsIndicator = new Bit2(0);
		escrFlag = new Bit1(0);
		esRateFlag = new Bit1(0);
		DSMTrickModeFlag = new Bit1(0);
		additionalCopyInfoFlag = new Bit1(0);
		CRCFlag = new Bit1(0);
		extensionFlag = new Bit1(0);
		// ptsが設定されているので、PTSFIeldを書き込む必要があるはず。
		PESHeaederLength = new Bit8(0);
	}
	@Override
	public void setupDefault() throws Exception {
		throw new Exception("こちら側のdefaultはつかいません。");
	}
	/**
	 * 動作をリセットしてみる。(たぶんつかわない。)
	 */
	public void reset() {
		rawData.position(0);
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		analyzeHeader(ch);
		if(isPayloadUnitStart()) {
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
					pts = new PtsField();
					dts = new DtsField();
					pts.analyze(ch);
					dts.analyze(ch);
					// pts
					length -= 10;
				}
				break;
			case 0x02:
				{
					// pts
					pts = new PtsField();
					pts.analyze(ch);
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
		}
		// 本来はこのあとデータを読み込む作業がでてくるはず。(順にデータパケットを流し込むと生成される的な感じでしょうか)
		rawData = BufferUtil.safeRead(ch, ch.size() - ch.position());
	}
	@Override
	public List<Bit> getBits() {
		List<Bit> list = super.getBits();
		if(isPayloadUnitStart()) {
			list.add(new Bit8(prefix >>> 16));
			list.add(new Bit8(prefix >>> 8));
			list.add(new Bit8(prefix));
			list.add(streamId);
			list.add(new Bit8(pesPacketLength >>> 8));
			list.add(new Bit8(pesPacketLength));
			list.add(markerBits);
			list.add(scramblingControl);
			list.add(priority);
			list.add(dataAlignmentIndicator);
			list.add(copyright);
			list.add(originFlg);
			list.add(ptsDtsIndicator);
			list.add(escrFlag);
			list.add(esRateFlag);
			list.add(DSMTrickModeFlag);
			list.add(additionalCopyInfoFlag);
			list.add(CRCFlag);
			list.add(extensionFlag);
			list.add(PESHeaederLength);
			switch(ptsDtsIndicator.get()) {
			case 0x03:
				// ptsもdtsもある場合
				pts.setSignature(new Bit4(3));
				list.addAll(pts.getBits());
				dts.setSignature(new Bit4(1));
				list.addAll(dts.getBits());
				break;
			case 0x02:
				// ptsのみの場合
				pts.setSignature(new Bit4(2));
				list.addAll(pts.getBits());
				break;
			default:
				// なにもなし。
				break;
			}
		}
		return list;
	}
	/**
	 * bufferデータを取得する。
	 * なお、nullが帰ってくるまで取得する必要ありとします。(そのデータのタグがすべて応答される。)
	 */
	@Override
	public ByteBuffer getBuffer() throws Exception {
		if(rawData == null) {
			throw new Exception("メディアデータの設定がなされていません。");
		}
		// 動作カウンターを上げる
		if(rawData.remaining() == 0) {
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(188);
		Integer counter = counterMap.get((int)getPid());
		if(counter == null) {
			counter = 0;
		}
		setContinuityCounter(counter);
		counter ++;
		counterMap.put((int)getPid(), counter);
		// ここは持っているデータをそのまま書き込めばOK
		// adaptationFieldの調整とか、出力データの調整はあとでnextPesでなんとかしておく。
		// bitデータについて調整しておく。
		List<Bit> bitsList = getBits();
		// bitデータを書き込む
		buffer.put(Bit.bitConnector(bitsList.toArray(new Bit[]{})));
		// データを書き込む
		int left = 188 - buffer.position();
		byte[] data = new byte[left]; // 残りの長さがrawDataに入っているデータとしておきます。
		rawData.get(data);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	public void setPesPacketLength(short length) {
		pesPacketLength = length;
	}
	public short getPesPacketLength() {
		return pesPacketLength;
	}
	public void setPts(PtsField pts) {
		this.pts = pts;
	}
	public void setDts(DtsField dts) {
		this.dts = dts;
	}
	public boolean hasPts() {
		return pts != null;
	}
	public boolean hasDts() {
		return dts != null;
	}
	/**
	 * 内部の生データを応答する。
	 * @return
	 */
	public ByteBuffer getRawData() {
		return rawData.duplicate();
	}
	public PtsField getPts() {
		return pts;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("Pes:").append(Integer.toHexString(getPid()));
		data.append("\n").append(super.toString());
		if(isPayloadUnitStart()) {
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
			if(pts != null) {
				data.append(pts);
			}
			if(dts != null) {
				data.append(dts);
			}
		}
		return data.toString();
	}
	/**
	 * 応答データの長さについて調査して、adaptationFieldとかを調整しておく。
	 * @param nextFlg
	 */
	private void checkLength(boolean nextFlg) {
		int length = 0;
		length += 4;
		// adaptationFieldの部分(任意)
		if(isAdaptationFieldExist()) {
			AdaptationField adaptationField = getAdaptationField();
			if(nextFlg) {
				adaptationField.setLength(1);
			}
			length += adaptationField.getLength() + 1;
		}
		if(isPayloadUnitStart()) {
			// prefix 3byte
			length += 3;
			// streamId 1byte
			length += 1;
			// pesPacketLength 2byte
			length += 2;
			// bitflags 2byte
			length += 2;
			// pesHeaderLength 1byte
			length += 1;
			// pesHeader実態 pesHeaderLength byte
			length += PESHeaederLength.get();
		}
		// はじめそのままいれれそうなデータ量
		int rawDataLength = pesPacketLength - 3 - PESHeaederLength.get();
		if(188 - length > rawDataLength) {
			// このパケットで埋め完了しそうな場合
			// ここで完了しそうなんだけど、adaptationFieldを追加したら完了できなくなるというシナリオもあり。
			if(!isAdaptationFieldExist()) {
				setAdaptationFieldExist(1);
			}
			AdaptationField adaptationField = getAdaptationField();
			adaptationField.setLength(adaptationField.getLength() + 188 - length - rawDataLength);
		}
	}
	/**
	 * 次のデータを応答します
	 */
	public Pes nextPes() throws Exception {
		// まだデータがあるか確認
		if(rawData.remaining() == 0) {
			return null;
		}
		Pes nextPes = new Pes(codec, false, false, getPid(), rawData);
		nextPes.setPayloadUnitStartIndicator(0);
		nextPes.checkLength(true);
		return nextPes;
	}
}
