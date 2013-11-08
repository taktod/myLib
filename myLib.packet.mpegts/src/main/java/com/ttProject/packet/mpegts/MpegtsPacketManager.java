package com.ttProject.packet.mpegts;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
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
 * この動作って、mpegtsの入力からmpegtsを作り出すものなので、xuggleとかつかった場合は
 * rawDataからmpegtsをつくる動作がほしくなるわけか・・・
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
	/** 処理済みpts値を保持 */
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
	/** 動作カウンター */
	private int counter = 0;
	/** 出力ターゲットファイル */
	private FileOutputStream fos = null;
	/**
	 * 全体停止時のため対処
	 */
	protected void finalize() throws Throwable {
		if(fos == null) {
			try {
				fos.close();
			}
			catch(Exception e) {
			}
			fos = null;
		}
	}
	/**
	 * パケットの内容を解析して必要な時間分のデータ(Packet)を応答します。
	 */
	protected IMediaPacket analizePacket(ByteBuffer buffer) {
		IReadChannel readChannel = new ByteReadChannel(buffer);
		Packet packet = null;
		MpegtsPacket mediaPacket = null;
		try {
			// mpegtsのパケットについて調査しておく
			while((packet = analyzer.analyze(readChannel)) != null) {
				if(packet instanceof Pat) {
					analyzePat((Pat)packet);
				}
				else if(packet instanceof Pmt) {
					analyzePmt((Pmt)packet);
				}
				else if(packet instanceof Pes) {
					mediaPacket = analyzePes((Pes)packet);
					if(mediaPacket != null) {
						break;
					}
				}
			}
			buffer.position(readChannel.position());
		}
		catch(Exception e) {
			logger.error("aiueo", e);
		}
		return mediaPacket;
	}
	/**
	 * patについて処理する
	 * @param pat
	 */
	private void analyzePat(Pat pat) {
		// pat指定がはじめての場合のみ受け入れる
		if(this.pat != null) {
			return;
		}
		this.pat = pat;
	}
	/**
	 * pmtについて処理する
	 * @param pmt
	 */
	private void analyzePmt(Pmt pmt) throws Exception {
		// pmt指定がはじめての場合のみ受け入れる
		if(this.pmt != null) {
			return;
		}
		this.pmt = pmt;
		for(PmtElementaryField field : pmt.getFields()) {
			// pidとコーデック情報を保持
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
	/**
	 * pesについて解析する。
	 * @param pes
	 * @throws Exception
	 */
	private MpegtsPacket analyzePes(Pes pes) throws Exception {
		// 処理済みpts値がまだ決まっていない場合は現在値を代入しておく。
		videoData.analyzePes(pes);
		audioData.analyzePes(pes);
		if(passedPts == -1) {
			if(!pes.hasPts()) {
				throw new Exception("ptsのない状態から開始しました。");
			}
			passedPts = pes.getPts().getPts();
		}
		long targetDuration = passedPts + (long)(90000L * getDuration()); // targetDuration以上たまっている場合は作成が成功する可能性があるので、作り始める。
		if(targetDuration < videoData.getLastDataPts()
		&& targetDuration < audioData.getLastDataPts()) {
			logger.info("必要以上にデータがたまったので書き込み実行");
			// keyframeとそれに従うデータをパケット化しておく。
			MpegtsPacket packet = (MpegtsPacket)getCurrentPacket();
			if(packet == null) {
				packet = new MpegtsPacket();
				// 先頭にあるべきデータをいれておく
				packet.analize(sdt.getBuffer());
				packet.analize(pat.getBuffer());
				packet.analize(pmt.getBuffer());
				setCurrentPacket(packet);
			}
			// keyframeとそれに従うデータをパケット化しておく。
			packet = makeKeyFrameUnit();
			if(packet != null) {
				return packet;
			}
		}
		return null;
	}
	/**
	 * keyFrameから次のkeyFrameまでのunitを設定しておきます。
	 * @throws Exception
	 */
	private MpegtsPacket makeKeyFrameUnit() throws Exception {
		// 映像のフレームを書き込む
		boolean isFirst = true; // 発動作フラグ
		// 補完する音声フレームを書き込む(ある程度以上にならない場合はスキップ)
		long audioStartPts = audioData.getFirstDataPts();
		List<IAudioData> audioDataList = new ArrayList<IAudioData>();
		int audioSize = 0;
		Pes videoPes = null;
		logger.info("フレーム書き込み開始");
		while((videoPes = videoData.shift()) != null) {
			if(videoPes.isPayloadUnitStart()) {
				logger.info("動画のペイロード");
				// payloadUnitの開始位置の場合
				if(!isFirst) {
					logger.info("音声書き込み");
					while(true) {
						IAudioData aData = audioData.shift();
						if(aData == null || audioData.getFirstDataPts() > videoPes.getPts().getPts()) {
							if(aData != null) {
								audioData.unshift(aData);
							}
							// たまったサイズを確認して書き込みを実行
							if(audioSize > 0x1000) {
								// 書き込み実行
								makeAudioPes(audioSize, audioDataList, audioStartPts);
								audioDataList.clear();
								audioSize = 0;
								audioStartPts = audioData.getFirstDataPts();
							}
							break;
						}
						audioSize += aData.getSize();
						audioDataList.add(aData);
					}
					// キーフレームでない場合はaudioDataを挿入したい。
					if(videoPes.isAdaptationFieldExist() && videoPes.getAdaptationField().getRandomAccessIndicator() == 1) {
						logger.info("キーフレーム発見");
						// 挿入処理後に確認してkeyFrameだったら、次のデータまできたことになる。
						videoData.unshift(videoPes);
						break;
					}
				}
				isFirst = false;
			}
			if(videoPes.isAdaptationFieldExist() && videoPes.hasPts()) {
				AdaptationField aField = videoPes.getAdaptationField();
				aField.setPcrBase(videoPes.getPts().getPts());
			}
			// videoPesの値はここで書き込みしてしまえばOK
			getCurrentPacket().analize(videoPes.getBuffer());
//			fos.getChannel().write(videoPes.getBuffer());
		}
		logger.info("ループをぬけました。");
		passedPts = audioData.getFirstDataPts(); // audioDataに残っているデータの終端位置をしっておきたい？
		// たまっているaudioデータがある場合は最後尾に追加しておく。
		if(audioSize > 0) {
			logger.info("音声の残りデータがあるので、書き込み実施します。");
			logger.info(audioSize);
			logger.info(audioDataList);
			logger.info(audioStartPts);
			// 書き込み実行
			makeAudioPes(audioSize, audioDataList, audioStartPts);
		}
		logger.info("フレーム書き込み完了。");
		// audioSampleの長さを確認して、対象データより大きくなっていたら完了として扱う。
		if(getCurrentPacket().getDuration() > getDuration()) {
			// duration以上にデータがたまっているなら、出来上がったことになります。
			MpegtsPacket packet = (MpegtsPacket)getCurrentPacket();
			setCurrentPacket(null);
			return packet;
		}
		else {
			return null;
		}
	}
	/**
	 * audio用のpesを作成します。
	 * @param audioSize
	 * @param audioDataList
	 * @param audioStartPts
	 * @throws Exception
	 */
	private void makeAudioPes(int audioSize, List<IAudioData> audioDataList, long audioStartPts) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(audioSize);
		MpegtsPacket mediaPacket = (MpegtsPacket)getCurrentPacket();
		for(IAudioData audioData : audioDataList) {
			mediaPacket.addSampleNum(audioData.getSampleNum());
			mediaPacket.setAudioSampleRate(audioData.getSampleRate());
			buffer.put(audioData.getRawData());
		}
		buffer.flip();
		Pes audioPes = new Pes(audioData.getCodecType(), 
				audioData.isPcr(), // pcrであるかはフラグ次第
				true, // randomAccessは絶対にOK(音声なので)
				audioData.getPid(), // pid
				buffer, // 実データ
				audioStartPts); // 開始pts
		do {
			mediaPacket.analize(audioPes.getBuffer());
		} while((audioPes = audioPes.nextPes()) != null);
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
