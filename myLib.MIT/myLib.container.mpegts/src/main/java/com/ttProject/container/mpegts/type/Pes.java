package com.ttProject.container.mpegts.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.MpegtsPacket;
import com.ttProject.container.mpegts.field.AdaptationField;
import com.ttProject.container.mpegts.field.DtsField;
import com.ttProject.container.mpegts.field.PtsField;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
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
	private Bit24 prefix = new Bit24(1); // 0x000001固定
	private Bit8 streamId = new Bit8(); // audioなら0xC0 - 0xDF videoなら0xE0 - 0xEFただしvlcが吐くデータはその限りではなかった。
	private Bit16 pesPacketLength = new Bit16();
	private Bit2 markerBits = new Bit2(2); // 10固定
	private Bit2 scramblingControl = new Bit2(); // 00
	private Bit1 priority = new Bit1(); // 0
	private Bit1 dataAlignmentIndicator = new Bit1(); // 0
	private Bit1 copyright = new Bit1(); // 0
	private Bit1 originFlg = new Bit1(); // 0:original 1:copy

	private Bit2 ptsDtsIndicator = new Bit2(); // ここ・・・wikiによると11だとboth、1だとPTSのみと書いてあるけど10の間違いではないですかね。
	private Bit1 escrFlag = new Bit1(); // 0
	private Bit1 esRateFlag = new Bit1(); // 0
	private Bit1 DSMTrickModeFlag = new Bit1(); // 0
	private Bit1 additionalCopyInfoFlag = new Bit1(); // 0
	private Bit1 CRCFlag = new Bit1(); // 0
	private Bit1 extensionFlag = new Bit1(); // 0

	private Bit8 PESHeaderLength = new Bit8(); // 10byte?
	private PtsField pts = null;
	private DtsField dts = null;
	
	/** このpesが保持しているデータ量 */
	private int pesDeltaSize = 0;

	/** unitの開始のpesは保持しておく。(こいつにframeを持たせることにする) */
	private Pes unitStartPes = null; // 主軸のpes
	// pesからデータを取り出すとこのframeListがとれるような感じにしておきたい。
	private IFrame frame = null;
	private ByteBuffer pesBuffer = null; // 実データ
	private int pesPacketLengthLeft = 0;
	private IAnalyzer frameAnalyzer = null;
	private boolean pcrFlag;
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
	 * @param pcrFlag
	 */
	public Pes(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter, boolean isPcr) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
		this.pcrFlag = isPcr;
	}
	/**
	 * コンストラクタ
	 */
	public Pes(int pid, boolean isPcr) {
		// 基本unitStartにしておきます。
		super(new Bit8(0x47), new Bit1(), new Bit1(1), new Bit1(),
				new Bit13(pid), new Bit2(), new Bit1(1), new Bit1(1), new Bit4());
		unitStartPes = this;
		this.pcrFlag = isPcr;
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
			unitStartPes = this;
		} // 844
		pesDeltaSize = 184 - (channel.position() - startPos);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// frameの実データを読み込みます。読み込んだデータはpayloadStartUnitをもっているpesに格納されます
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
					addFrame(frame);
				}
			}
		}
		else {
			logger.info("残りデータ:" + unitStartPes.pesPacketLengthLeft);
		}
	}
	/**
	 * 動作フレームを参照する
	 * @return
	 */
	public IFrame getFrame() {
		return unitStartPes.frame;
	}
	/**
	 * frameを追加する
	 * @param tmpFrame
	 */
	public void addFrame(IFrame tmpFrame) throws Exception {
		if(frame == null) {
			frame = tmpFrame;
		}
		else if(frame instanceof AudioMultiFrame) {
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("audioFrameの追加バッファとしてaudioFrame以外を受け取りました");
			}
			((AudioMultiFrame)frame).addFrame((IAudioFrame)tmpFrame);
		}
		else if(frame instanceof VideoMultiFrame) {
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("videoFrameの追加バッファとしてvideoFrame以外を受け取りました");
			}
			((VideoMultiFrame)frame).addFrame((IVideoFrame)tmpFrame);
		}
		else if(frame instanceof IAudioFrame) {
			AudioMultiFrame multiFrame = new AudioMultiFrame();
			multiFrame.addFrame((IAudioFrame)frame);
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("audioFrameの追加バッファとしてaudioFrame以外を受け取りました");
			}
			multiFrame.addFrame((IAudioFrame)tmpFrame);
			frame = multiFrame;
		}
		else if(frame instanceof IVideoFrame) {
			VideoMultiFrame multiFrame = new VideoMultiFrame();
			multiFrame.addFrame((IVideoFrame)frame);
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("videoFrameの追加バッファとしてvideoFrame以外を受け取りました");
			}
			multiFrame.addFrame((IVideoFrame)tmpFrame);
			frame = multiFrame;
		}
		else {
			throw new Exception("frameのデータに不明なデータがはいりました。");
		}
	}
	@Override
	protected void requestUpdate() throws Exception {
		// TODO 保持frameからByteBufferのデータを復元します。
		// unitStartPesのみ動作可能としたいとおもいます。
		if(!isPayloadUnitStart()) {
			throw new Exception("データの取得はunitStartのpesから実行してください。");
		}
		// 時間まわりの設定調整
		if(frame instanceof IAudioFrame) {
			// ptsのみ
			setPts(frame.getPts(), frame.getTimebase());
			ptsDtsIndicator.set(2);
			PESHeaderLength.set(5);
			// adaptationFieldを有効にして、randomAccessIndicatorとpcrの設定が必要になる。
			if(pcrFlag) {
				setAdaptationFieldExist(1);
				AdaptationField aField = getAdaptationField();
				aField.setRandomAccessIndicator(1);
				aField.setPcrFlag(1);
				aField.setPcrBase((long)(1.0D * frame.getPts() / frame.getTimebase() * 90000));
				// 63000
			}
		}
		else if(frame instanceof IVideoFrame) {
			// pts
			setPts(frame.getPts(), frame.getTimebase());
			// dtsあるかもしれない
			IVideoFrame vFrame = (IVideoFrame)frame;
			if(vFrame.getDts() != -1) {
				// dtsが存在するので書き込みしておく。
				setDts(vFrame.getDts(), vFrame.getTimebase());
				ptsDtsIndicator.set(3);
				PESHeaderLength.set(10);
			}
			else {
				ptsDtsIndicator.set(2);
				PESHeaderLength.set(5);
			}
			// keyFrameなら
			// adaptationFieldにpcrをいれる必要あり。
			if(vFrame.isKeyFrame() && pcrFlag) {
				setAdaptationFieldExist(1);
				AdaptationField aField = getAdaptationField();
				aField.setRandomAccessIndicator(1);
				aField.setPcrFlag(1);
				aField.setPcrBase((long)(1.0D * frame.getPts() / frame.getTimebase() * 90000));
			}
		}
		else {
			throw new Exception("frameに映像でも音声でもない情報がはいっていました。");
		}
		// pesPacketLengthを更新する。
		pesPacketLength.set(3 + PESHeaderLength.get() + frame.getSize());

		// 登録すべきframeデータ
		ByteBuffer buffer = ByteBuffer.allocate(frame.getSize());
		if(frame instanceof AudioMultiFrame) {
			for(IFrame audioFrame : ((AudioMultiFrame)frame).getFrameList()) {
				buffer.put(audioFrame.getData());
			}
		}
		else if(frame instanceof VideoMultiFrame) {
			for(IFrame videoFrame : ((VideoMultiFrame)frame).getFrameList()) {
				buffer.put(videoFrame.getData());
			}
		}
		else {
			buffer.put(frame.getData());
		}
		buffer.flip();
		buffer.remaining();
		// header部分のサイズがどのくらいあるか確認しておく。
		/*
		 * 先頭4byte
		 * adaptationFieldがあるなら、getLength
		 * 9 + PESHeaderLength.get()
		 * これが先頭にあるデータ量
		 */
		int headerLength = (isAdaptationFieldExist() ? getAdaptationField().getLength() : 0) + 9 + PESHeaderLength.get();
		// 必要なデータ量をしらべないとだめ。
		logger.info("frameSize:" + buffer.remaining());
		logger.info((int)(Math.ceil((buffer.remaining() + headerLength) / 184f) * 188));
		ByteBuffer pesChunk = ByteBuffer.allocate((int)(Math.ceil((buffer.remaining() + headerLength) / 184f) * 188));
		if(headerLength + buffer.remaining() < 184) {
			// 1packetで済む長さなので、調整しないとだめ。
			logger.info("1packetで済む長さなので、adaptationFieldで調整します。");
			return;
		}
		logger.info("通常のパケットなので、まず第１パケットの書き込みを実行します。");
		// データサイズを計算します。
		// header4byteとadaptationFieldを取得します。
		pesChunk.put(getHeaderBuffer());
		BitConnector connector = new BitConnector();
		connector.feed(prefix, streamId, pesPacketLength, markerBits,
				scramblingControl, priority, dataAlignmentIndicator,
				copyright, originFlg, ptsDtsIndicator, escrFlag,
				esRateFlag, DSMTrickModeFlag, additionalCopyInfoFlag,
				CRCFlag, extensionFlag, PESHeaderLength);
		switch(ptsDtsIndicator.get()) {
		case 0:
			break;
		case 2:
			logger.info(pts.getBits());
			connector.feed(pts.getBits());
			break;
		case 3:
			connector.feed(pts.getBits());
			connector.feed(dts.getBits());
			break;
		default:
		}
		pesChunk.put(connector.connect());
		byte[] data = new byte[(188 - (pesChunk.position() % 188))];
		buffer.get(data);
		pesChunk.put(data);
		// 次のデータをいれていく。
		// adaptationFieldをoffにしておく。
		AdaptationField aField = getAdaptationField();
		aField.setPcrFlag(0);
		aField.setRandomAccessIndicator(0);
		// adaptationFieldがないものとする
		setAdaptationFieldExist(0);
		// payloadUnitStartでないとする。
		setPayloadUnitStart(0);
		// pesのunitを書き込んでいく
		while(buffer.remaining() > 0) {
			logger.info(pesChunk.position());
			setContinuityCounter(getContinuityCounter() + 1);
			if(buffer.remaining() < 184) {
				logger.info("here...:" + buffer.remaining());
				setAdaptationFieldExist(1);
				getAdaptationField().setLength(183 - buffer.remaining());
				logger.info(getAdaptationField());
				pesChunk.put(getHeaderBuffer().array());
				data = new byte[buffer.remaining()];
				buffer.get(data);
				pesChunk.put(data);
				break;
			}
			else {
				pesChunk.put(getHeaderBuffer().array());
				data = new byte[184];
			}
			buffer.get(data);
			pesChunk.put(data);
		}
		pesChunk.flip();
		// header部checkOK
//		logger.info(HexUtil.toHex(pesChunk, true));
		setData(pesChunk);
		// pesの内容をもとに戻しておく。
	}
	private void setPts(long timestamp, long timebase) {
		pts = new PtsField();
		pts.setPts((long)(1.0D * timestamp / timebase * 90000));
		logger.info("pts:" + getPts());
	}
	private void setDts(long timestamp, long timebase) {
		dts = new DtsField();
		dts.setDts((long)(1.0D * timestamp / timebase * 90000));
	}
	public void setStreamId(int id) {
		streamId.set(id);
	}
}
