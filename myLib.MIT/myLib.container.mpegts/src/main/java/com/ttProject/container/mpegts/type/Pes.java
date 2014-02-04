package com.ttProject.container.mpegts.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.MpegtsPacket;
import com.ttProject.container.mpegts.field.AdaptationField;
import com.ttProject.container.mpegts.field.DtsField;
import com.ttProject.container.mpegts.field.PtsField;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.SliceFrame;
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
 * frameの追加はいけるけど、requestUpdateが長すぎるので、調整しておきたい。
 */
public class Pes extends MpegtsPacket {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Pes.class);
	private Bit24 prefix = new Bit24(1); // 0x000001固定
	private Bit8  streamId = new Bit8(); // audioなら0xC0 - 0xDF videoなら0xE0 - 0xEFただしvlcが吐くデータはその限りではなかった。
	private Bit16 pesPacketLength = new Bit16();
	private Bit2  markerBits = new Bit2(2); // 10固定
	private Bit2  scramblingControl = new Bit2(); // 00
	private Bit1  priority = new Bit1(); // 0
	private Bit1  dataAlignmentIndicator = new Bit1(); // 0
	private Bit1  copyright = new Bit1(); // 0
	private Bit1  originFlg = new Bit1(); // 0:original 1:copy

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
	private List<ByteBuffer> pesBufferList = null;
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
				new Bit13(pid), new Bit2(), new Bit1(0), new Bit1(1), new Bit4());
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		int startPos = channel.position();
		super.minimumLoad(channel);
		// payloadStartUnitの場合はデータの読み込みがあるみたいです。
		if(isPayloadUnitStart()) {
			// 開始なので、各種情報があると思われる
			BitLoader loader = new BitLoader(channel);
			loader.load(prefix, streamId, pesPacketLength, markerBits,
					scramblingControl, priority, dataAlignmentIndicator,
					copyright, originFlg, ptsDtsIndicator, escrFlag,
					esRateFlag, DSMTrickModeFlag, additionalCopyInfoFlag,
					CRCFlag, extensionFlag, PESHeaderLength);
			// BufferListをつくっておきます
			pesBufferList = new ArrayList<ByteBuffer>();
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
		}
		pesDeltaSize = 184 - (channel.position() - startPos);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// frameの実データを読み込みます。読み込んだデータはpayloadStartUnitをもっているpesに格納されます
		unitStartPes.pesBufferList.add(BufferUtil.safeRead(channel, pesDeltaSize));
	}
	/**
	 * データの解析を実行させます
	 */
	public void analyzeFrame() throws Exception {
		if(unitStartPes.frameAnalyzer != null) {
			IReadChannel pesBufferChannel = new ByteReadChannel(BufferUtil.connect(unitStartPes.pesBufferList));
			IFrame frame = null;
			long audioSampleNum = 0;
			while((frame = unitStartPes.frameAnalyzer.analyze(pesBufferChannel)) != null) {
				if(frame instanceof VideoFrame) {
					VideoFrame vFrame = (VideoFrame)frame;
					vFrame.setPts(unitStartPes.pts.getPts());
					vFrame.setTimebase(90000);
				}
				else if(frame instanceof AudioFrame) {
					AudioFrame aFrame = (AudioFrame)frame;
					aFrame.setPts(unitStartPes.pts.getPts() + audioSampleNum * 90000 / aFrame.getSampleRate());
					aFrame.setTimebase(90000);
					audioSampleNum += aFrame.getSampleNum();
				}
				unitStartPes.addFrame(frame);
			}
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
		if(tmpFrame == null) {
			return;
		}
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
				throw new Exception("videoFrameの追加バッファとしてvideoFrame以外を受け取りました:" + tmpFrame);
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
		// TODO このタイミングでupdateフラグを更新しておかないと、フレームを追加したときに、データがきちんととれないみたいです。
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(frame == null) {
			throw new Exception("frameデータがありません。");
		}
		// unitStartPesのみ動作可能としたいとおもいます。
		if(!isPayloadUnitStart()) {
			throw new Exception("データの取得はunitStartのpesから実行してください。");
		}
		// 時間に関する設定更新
		setupTime();
		// frameデータ取得
		ByteBuffer frameBuffer = frameBuffer();
		// pesPacketLengthを更新する。
		if(3 + PESHeaderLength.get() + frameBuffer.remaining() > 0x010000) {
			pesPacketLength.set(0);
		}
		else {
			pesPacketLength.set(3 + PESHeaderLength.get() + frameBuffer.remaining());
		}

		// header部分のサイズがどのくらいあるか確認しておく。
		/*
		 * 先頭4byte
		 * adaptationFieldがあるなら、getLength
		 * 9 + PESHeaderLength.get()
		 * これが先頭にあるデータ量
		 */
		// adaptationFieldはadapatationFieldSizeが抜けているので+1しなければいけない。
		int headerLength = (isAdaptationFieldExist() ? getAdaptationField().getLength() + 1 : 0) + 9 + PESHeaderLength.get();
		// 必要なデータ量をしらべないとだめ。
		ByteBuffer pesChunk = ByteBuffer.allocate((int)(Math.ceil((frameBuffer.remaining() + headerLength) / 184f) * 188));
		if(headerLength + frameBuffer.remaining() < 184) {
			// 1packetで済む長さなので、調整しないとだめ。
			setAdaptationFieldExist(1); // adaptationFieldを有効にする。
			AdaptationField aField = getAdaptationField();
			aField.setLength(aField.getLength() + 183 - (headerLength + frameBuffer.remaining()));
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
				connector.feed(pts.getBits());
				break;
			case 3:
				connector.feed(pts.getBits());
				connector.feed(dts.getBits());
				break;
			default:
			}
			pesChunk.put(connector.connect());
			pesChunk.put(frameBuffer);
			pesChunk.flip();
			super.setData(pesChunk);
			return;
		}
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
		frameBuffer.get(data);
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
		while(frameBuffer.remaining() > 0) {
			setContinuityCounter(getContinuityCounter() + 1);
			if(frameBuffer.remaining() < 184) {
				setAdaptationFieldExist(1);
				getAdaptationField().setLength(183 - frameBuffer.remaining());
				pesChunk.put(getHeaderBuffer().array());
				data = new byte[frameBuffer.remaining()];
				frameBuffer.get(data);
				pesChunk.put(data);
				break;
			}
			else {
				pesChunk.put(getHeaderBuffer().array());
				data = new byte[184];
			}
			frameBuffer.get(data);
			pesChunk.put(data);
		}
		pesChunk.flip();
		// header部checkOK
		setData(pesChunk);
		// pesの内容をもとに戻しておく。
	}
	/**
	 * 書き込みを実行すべきframeデータ
	 * @return
	 * @throws Exception
	 */
	private ByteBuffer frameBuffer() throws Exception {
		ByteBuffer frameBuffer = null;
		if(frame instanceof IAudioFrame) {
			// 音声フレームの場合
			frameBuffer = ByteBuffer.allocate(frame.getSize());
			if(frame instanceof AudioMultiFrame) {
				// マルチフレームの場合は結合しなければいけない。
				for(IAudioFrame audioFrame : ((AudioMultiFrame)frame).getFrameList()) {
					if(audioFrame.getData() == null) { // なぜかたまに内容データがnullのAudioFrameがくるっぽい・・・うーん
						continue;
					}
					frameBuffer.put(audioFrame.getData());
				}
			}
			else {
				// 単体フレームの場合はそのまま
				frameBuffer.put(frame.getData());
			}
			frameBuffer.flip();
			return frameBuffer;
		}
		else if(frame instanceof IVideoFrame) {
			// 映像フレームの場合
			if(frame instanceof H264Frame) {
				// これはまずありえない。
				throw new Exception("h264Frameが単体で設定されています。なにかがおかしいです。:" + frame);
			}
			else if(frame instanceof VideoMultiFrame) {
				// 結合すべきデータ長を計算しなければならない。
				int length = 0;
				VideoMultiFrame multiFrame = (VideoMultiFrame)frame;
				boolean findSliceFrame = false;
				for(IVideoFrame videoFrame : multiFrame.getFrameList()) {
					if(videoFrame instanceof SliceFrame) {
						if(findSliceFrame) {
							length += 3 + videoFrame.getSize(); // 同じframeの２つ目だったら3になるっぽいですね。
						}
						else {
							length += 4 + videoFrame.getSize(); // 同じframeの２つ目だったら3になるっぽいですね。
						}
						findSliceFrame = true;
					}
					else if(videoFrame instanceof H264Frame) {
						// TODO 各フレームの１つ目は00 00 00 01がいい。
						// TODO 同一フレームが２つ以上はいっている場合の２つ目は00 00 01になるらしい。
						length += 4 + videoFrame.getSize(); // 同じframeの２つ目だったら3になるっぽいですね。
					}
					else {
						throw new Exception("h264以外のフレームがmultiFrameに混入していました。:" + videoFrame);
					}
				}
				// メモリー確保
				frameBuffer = ByteBuffer.allocate(length);
				// データ登録
				findSliceFrame = false;
				for(IVideoFrame videoFrame : multiFrame.getFrameList()) {
					if(videoFrame instanceof SliceFrame) {
						if(findSliceFrame) {
							frameBuffer.put((byte)0x00);
							frameBuffer.putShort((short)1);
							frameBuffer.put(videoFrame.getData());
						}
						else {
							frameBuffer.putInt(1);
							frameBuffer.put(videoFrame.getData());
						}
						findSliceFrame = true;
					}
					else if(videoFrame instanceof H264Frame) {
						frameBuffer.putInt(1);
						frameBuffer.put(videoFrame.getData());
					}
					else {
						throw new Exception("h264以外のフレームがmultiFrameに混入していました。:" + videoFrame);
					}
				}
			}
			else {
				throw new Exception("知らないframeデータでした。:" + frame);
			}
			frameBuffer.flip();
			return frameBuffer;
		}
		else {
			throw new Exception("pesが映像でも音声でもないデータを保持していました。:" + frame);
		}
	}
	/**
	 * 時間に関する設定を実行しておく。
	 */
	private void setupTime() throws Exception {
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
			if(vFrame.getDts() != -1 && vFrame.getDts() != 0) {
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
			throw new Exception("frameに映像でも音声でもない情報がはいっていました。:" + frame);
		}
	}
	private void setPts(long timestamp, long timebase) {
		pts = new PtsField();
		pts.setPts((long)(1.0D * timestamp / timebase * 90000));
	}
	private void setDts(long timestamp, long timebase) {
		dts = new DtsField();
		dts.setDts((long)(1.0D * timestamp / timebase * 90000));
	}
	public void setStreamId(int id) {
		streamId.set(id);
	}
	public int getStreamId() {
		return streamId.get();
	}
}
