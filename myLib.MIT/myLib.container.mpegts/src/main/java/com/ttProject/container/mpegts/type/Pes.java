package com.ttProject.container.mpegts.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.MpegtsPacket;
import com.ttProject.container.mpegts.field.DtsField;
import com.ttProject.container.mpegts.field.PtsField;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
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
 * 
 * とりあえずpesからIFrameを取り出す必要がでてくる予定ですが、
 * pesデータは、payloadStartのpesを保持させることにして、そこにデータをためていく。
 * みたいな形にしておきたいと思います。
 * 満了したらframeを解析するみたいな。
 */
public class Pes extends MpegtsPacket {
	/** ロガー */
	private Logger logger = Logger.getLogger(Pes.class);
	private Bit24 prefix = null; // 0x000001固定
	private Bit8 streamId = null; // audioなら0xC0 - 0xDF videoなら0xE0 - 0xEFただしvlcが吐くデータはその限りではなかった。
	private Bit16 pesPacketLength = null;
	private Bit2 markerBits = null; // 10固定
	private Bit2 scramblingControl = null; // 00
	private Bit1 priority = null; // 0
	private Bit1 dataAlignmentIndicator = null; // 0
	private Bit1 copyright = null; // 0
	private Bit1 originFlg = null; // 0:original 1:copy

	private Bit2 ptsDtsIndicator = null; // ここ・・・wikiによると11だとboth、1だとPTSのみと書いてあるけど10の間違いではないですかね。
	private Bit1 escrFlag = null; // 0
	private Bit1 esRateFlag = null; // 0
	private Bit1 DSMTrickModeFlag = null; // 0
	private Bit1 additionalCopyInfoFlag = null; // 0
	private Bit1 CRCFlag = null; // 0
	private Bit1 extensionFlag = null; // 0

	private Bit8 PESHeaderLength = null; // 10byte?
	private PtsField pts = null;
	private DtsField dts = null;
	
	/** このpesが保持しているデータ量 */
	private int pesDeltaSize = 0;

	/** unitの開始のpesは保持しておく。(こいつにframeを持たせることにする) */
	private Pes unitStartPes = null; // 主軸のpes
	// pesからデータを取り出すとこのframeListがとれるような感じにしておきたい。
	private List<IFrame> frameList = null; // 主軸のpesにframeListを持たせる必要があります。
	private ByteBuffer pesBuffer = null; // 実データ
	private int pesPacketLengthLeft = 0;
	private IAnalyzer frameAnalyzer = null;
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
	public Pes(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
	}
	/**
	 * 開始位置のpesを保持しておく
	 * @param pes
	 */
	public void setUnitStartPes(Pes pes) {
		unitStartPes = pes;
	}
	public void setFrameAnalyzer(IAnalyzer analyzer) {
		frameAnalyzer = analyzer;
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		int startPos = channel.position();
		super.minimumLoad(channel);
		// payloadStartUnitの場合はデータの読み込みがあるみたいです。
		if(isPayloadUnitStart()) {
			logger.info("payloadの開始である");
			// 開始なので、各種情報があると思われる
			prefix = new Bit24();
			streamId = new Bit8();
			pesPacketLength = new Bit16();
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
			PESHeaderLength = new Bit8();
			BitLoader loader = new BitLoader(channel);
			loader.load(prefix, streamId, pesPacketLength, markerBits,
					scramblingControl, priority, dataAlignmentIndicator,
					copyright, originFlg, ptsDtsIndicator, escrFlag,
					esRateFlag, DSMTrickModeFlag, additionalCopyInfoFlag,
					CRCFlag, extensionFlag, PESHeaderLength);
			pesPacketLengthLeft = pesPacketLength.get() - 3 - PESHeaderLength.get(); // このあとのデータも含むので(その分引かないとだめ(3とheaderLength分))
			pesBuffer = ByteBuffer.allocate(pesPacketLengthLeft);
			int length = PESHeaderLength.get();
			switch(ptsDtsIndicator.get()) {
			case 0x03:
				pts = new PtsField();
				dts = new DtsField();
				pts.load(channel);
				dts.load(channel);
				length -= 10;
				break;
			case 0x02:
				pts = new PtsField();
				pts.load(channel);
				length -= 5;
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
			frameList = new ArrayList<IFrame>();
		} // 844
		pesDeltaSize = 184 - (channel.position() - startPos);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// frameの実データを読み込みます。読み込んだデータはpayloadStartUnitをもっているpesに格納されます
		if(unitStartPes == null) {
			unitStartPes = this;
			logger.info("読み込むデータ量:" + unitStartPes.pesPacketLengthLeft);
		}
		unitStartPes.pesBuffer.put(BufferUtil.safeRead(channel, pesDeltaSize));
		// ここでは読み込んだデータを主体となるpesのdata領域に格納させていきます。
		unitStartPes.pesPacketLengthLeft -= pesDeltaSize;
		if(unitStartPes.pesPacketLengthLeft == 0) {
			logger.info("最後まで読み込めた");
			// ここまできたら、byteBufferからframeを生成して保持しておけばよい。
			if(unitStartPes.frameAnalyzer != null) {
				unitStartPes.pesBuffer.flip();
				IReadChannel pesBufferChannel = new ByteReadChannel(unitStartPes.pesBuffer);
				IFrame frame = null;
				while((frame = unitStartPes.frameAnalyzer.analyze(pesBufferChannel)) != null) {
					unitStartPes.frameList.add(frame);
				}
			}
		}
		else {
			logger.info("残りデータ:" + unitStartPes.pesPacketLengthLeft);
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
