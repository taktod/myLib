package com.ttProject.packet.mpegts;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.IAudioData;
import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.media.mpegts.field.AdaptationField;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pat;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pmt;
import com.ttProject.media.mpegts.packet.Sdt;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.packet.IMediaPacket;
import com.ttProject.packet.MediaPacketManager;

/**
 * 基本的にデータを受け取ったらそのデータをメモリーにとっておいて、必要な秒数分の音声と映像データが入手できたらOKみたいな感じ。
 * 音声データは必要があれば、分解して再構築する必要あり。
 * このパケットデータが指定された秒数分のファイルデータとなります。
 * Sdt Pat Pmt [keyFrame Audio innerFrame] [keyFrame Audio innerFrame]
 * となるようにしておきたいと思います。
 * @author taktod
 */
public class MpegtsPacketManager extends MediaPacketManager {
	private final Logger logger = Logger.getLogger(MpegtsPacketManager.class);
	/** sdtはねつ造します。 */
	private final Sdt sdt;
	/** patは本家のものを利用します(ただし更新はしません。) */
	private Pat pat;
	/** pmtも本家のものを利用します(ただし更新はしません。) */
	private Pmt pmt;
	/** 映像用データのpes保持 */
	private final VideoData videoData = new VideoData();
	/** 音声用データのpes保持 */
	private final AudioData audioData = new AudioData();
	private long passedPts = -1; // 処理済みpts値保持
	/**
	 * コンストラクタ
	 */
	public MpegtsPacketManager() throws Exception {
		sdt = new Sdt();
		sdt.writeDefaultProvider("taktodTools", "mpegtsMuxer");
	}
	// analyzerは外にだしておかないと、初期化時のデータがなくなってエラーになることがあるっぽいですね。
	private IPacketAnalyzer analyzer = new PacketAnalyzer();
	private int counter = 0;
	/**
	 * パケットの内容を解析して、必要な時間分のデータ(Packetを応答します)
	 */
	@Override
	protected IMediaPacket analizePacket(ByteBuffer buffer) {
		logger.info("analyzePacket here...:" + buffer.remaining());
//		logger.info(HexUtil.toHex(buffer.duplicate(), 0, 20, true));
		IReadChannel readChannel = new ByteReadChannel(buffer);
		Packet packet = null;
		try {
			while((packet = analyzer.analyze(readChannel)) != null) {
				if(packet instanceof Pat) {
					if(pat == null) {
						pat = (Pat)packet;
					}
				}
				else if(packet instanceof Pmt) {
					if(pmt == null) {
						pmt = (Pmt)packet;
						for(PmtElementaryField field : pmt.getFields()) {
							// pidとコーデック情報を保持しておく。
							// 音声データと映像データが、ある程度以上存在しないとどうしようもない。
							logger.info(field.getCodecType());
							switch(field.getCodecType()) {
							case VIDEO_H264:
								videoData.analyzePmt(pmt, field);
								break;
							case AUDIO_AAC:
							case AUDIO_MPEG1:
								audioData.analyzePmt(pmt, field);
								break;
							default:
								break;
							}
						}
					}
				}
				else if(packet instanceof Pes) {
					Pes pes = (Pes)packet;
					if(passedPts == -1) {
						if(pes.hasPts()) {
							passedPts = pes.getPts().getPts();
						}
					}
					videoData.analyzePes(pes);
					audioData.analyzePes(pes);
					long targetDuration = passedPts + (long)(90000 * getDuration());
					if(targetDuration < videoData.getLastDataPts()
					&& targetDuration < audioData.getLastDataPts()) {
						// 音声も映像もduration分以上たまったので・・・ファイルに吐いてOK
						logger.info(targetDuration);
						logger.info(videoData.getLastDataPts());
						logger.info(audioData.getLastDataPts());
						
						boolean keyFrame = true; // keyFrameデータフラグ
						long frameEndPts = -1;
						// TODO とりあえずここで分割tsを仮につくってみる。
						FileChannel fc = new FileOutputStream("test" + (++counter) + ".ts").getChannel();
						fc.write(sdt.getBuffer());
						fc.write(pat.getBuffer());
						fc.write(pmt.getBuffer());
						// 映像の間にいい感じに音声データを挟めばいいはず。pcrや時間の同期に注意といったところか？
						while(true) {
							Pes videoPes = videoData.shift();
							if(videoPes == null) {
								break;
							}
							if(videoPes.isPayloadUnitStart()) {
								if(videoPes.isAdaptationFieldExist() && videoPes.getAdaptationField().getRandomAccessIndicator() == 1) {
									// キーフレーム
									if(!keyFrame) {
										// すでに別のフレームの確認結果だった場合
										// このキーフレームは次の処理で対処すべき
										frameEndPts = videoPes.getPts().getPts();
										// データは返しておく。
										videoData.unshift(videoPes);
										break;
									}
								}
								else {
									keyFrame = false;
								}
								// PCRのデータは書き直してやった方がいいのだろうか？
								if(videoPes.isAdaptationFieldExist()) {
									AdaptationField afield = videoPes.getAdaptationField();
									afield.setPcrBase(videoPes.getPts().getPts());
								}
							}
							fc.write(videoPes.getBuffer());
						}
						// audioのpts値を計算する。(1足す必要はなさそう。)
						long audioStartPts = audioData.getFirstDataPts() + 1;
						List<IAudioData> aDataList = new ArrayList<IAudioData>();
						int size = 0;
						// ここはおおいに改良の余地あり。とりあえず時間同期がややこしいが・・・
						while(true) {
							IAudioData aData = audioData.shift();
							// 残りのデータの先頭データのptsが動画frameのptsを超した場合、とりすぎとなる。
							if(aData == null || audioData.getFirstDataPts() > frameEndPts) {
								audioData.unshift(aData);
								break;
							}
							size += aData.getSize();
							aDataList.add(aData);
/*							if(size > 0x1000) {
								ByteBuffer buf =ByteBuffer.allocate(size);
								for(IAudioData aD : aDataList) {
									buf.put(aD.getRawData());
								}
								buf.flip();
								// audio用のpesを作成する。
								Pes audioPes = new Pes(audioData.getCodecType(), audioData.isPcr(), true, audioData.getPid(), buf, audioStartPts);
								do {
									fc.write(audioPes.getBuffer());
								}
								while((audioPes = audioPes.nextPes()) != null);
								size = 0;
								aDataList.clear();
								audioStartPts = audioData.getFirstDataPts();
							}*/
						}
						if(size > 0) {
							ByteBuffer buf =ByteBuffer.allocate(size);
							for(IAudioData aData : aDataList) {
								buf.put(aData.getRawData());
							}
							buf.flip();
							// audio用のpesを作成する。
							Pes audioPes = new Pes(audioData.getCodecType(), audioData.isPcr(), true, audioData.getPid(), buf, audioStartPts);
							do {
								fc.write(audioPes.getBuffer());
							}
							while((audioPes = audioPes.nextPes()) != null);
						}
						fc.close();
						passedPts = frameEndPts;
//						System.exit(0);
					}
				}
			}
			buffer.position(readChannel.position());
		}
		catch(Exception e) {
			logger.error("", e);
			System.exit(0);
		}
		// 処理中のパケットデータを参照
		// 処理中のパケットデータがなければ、新しいパケットデータを作成
		// 処理中のパケットデータにデータを追加
		// 指定秒数以上データがたまっていたら応答を実施する。
		// でOK
		// manager側でmpegtsのデータを管理して、送った方がいいと思われます。
		return null;
	}
	/**
	 * 拡張子応答
	 */
	@Override
	public String getExt() {
		return ".ts";
	}
	/**
	 * リストファイルの拡張子応答
	 */
	@Override
	public String getHeaderExt() {
		return ".m3u8";
	}
}
