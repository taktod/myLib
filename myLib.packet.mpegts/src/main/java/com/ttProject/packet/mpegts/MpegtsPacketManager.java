package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
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
					if(targetDuration < videoData.getStackedDataPts()
					&& targetDuration < audioData.getStackedDataPts()) {
						// 音声も映像もduration分以上たまったので・・・ファイルに吐いてOK
						logger.info(targetDuration);
						logger.info(videoData.getStackedDataPts());
						logger.info(audioData.getStackedDataPts());
						// まずh264のkeyとなるPesデータについて取得する。
						// TODO みつけたデータはpacketオブジェクトに持たせておけばいいか？
						// pesデータを取り出す。(終端まできたときにどうするかが問題だが・・・)
						while(true) {
							// 次のmpegtsの値が決定しないと、audioDataの取得すべきデータ量が決まらない。
							Pes videoPes = videoData.shift();
							break;
						}
						// h264のinnerFrameとなるPesデータについて取得する。
						// aacの内部に挿入すべきデータを取得する。
						// h264のkey用データを書き込む
						// aacのpesを書き込む
						// h264のinner用データを書き込む
						// おわり。 
						System.exit(0);
					}
				}
			}
			buffer.position(readChannel.position());
		}
		catch(Exception e) {
			logger.error(e);
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
