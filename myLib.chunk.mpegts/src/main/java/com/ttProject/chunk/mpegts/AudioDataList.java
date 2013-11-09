package com.ttProject.chunk.mpegts;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.IAudioData;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * audioDataはunitごとの動作への切り分けが比較的容易に実行できるので、unitごとの保存でいい
 * @author taktod
 */
public class AudioDataList extends MediaDataList {
	/** ロガー */
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AudioDataList.class);
	/** 保持データリスト(aacやmp3の状態に分解されています。) */
	private final List<IAudioData> audioDataList = new ArrayList<IAudioData>();
	/** 取得サンプル数 */
	private long counter = 0;
	/** 転送済みのサンプル数 */
	private long sendedCounter = 0;
	/** pesの開始位置(mpegtsのtimestampは必ずしも0からはじまっていないので、ずれ分を保持しなければいけない) */
	private long startPos = 0;
	/** sampleRate値(時間の計算で必要) */
	private int sampleRate = 44100;
	/** pesからデータを取得する場合に必要になる処置(一度ためてからIAudioDataに分解して、再構築する必要がでてくる) */
//	private final List<Pes> pesList = new ArrayList<Pes>();
	/**
	 * 保持データリストのサイズを確認する。
	 * @return
	 */
	public int getListCount() {
		return audioDataList.size();
	}
	/**
	 * データを追加する。
	 * @param data データ実体
	 * @param pts pts値(任意)
	 * @param pid pid値(任意)
	 * @param type codecType(任意)
	 */
	public void addAudioData(IAudioData data, long pts, short pid, CodecType type) {
/*		if(getPid() != pes.getPid() // 自分のデータではない
			|| getCodecType() == null) { // 初期化が済んでいない
			return; // 処理しない
		}
		if(startPos == -1 && pes.hasPts()) {
			// 開始位置が未決定でpts値があるデータの場合
			startPos = pes.getPts().getPts();
		}
		if(pes.isPayloadUnitStart()) {
			// 前のデータがある場合はデータが決定するので、ここでIAudioDataに分解する
			ByteBuffer buffer = ByteBuffer.allocate(188 * pesList.size());
			while(pesList.size() > 0) {
				Pes p = pesList.remove(0);
				buffer.put(p.getRawData());
			}
			buffer.flip(); // ここまででbufferデータが決定する。
			IReadChannel bufferChannel = null;
			try {
				bufferChannel = new ByteReadChannel(buffer);
				switch(getCodecType()) {
				case AUDIO_AAC:
					// ここでaacをIAudioDataに戻してやりたい。
					IFrameAnalyzer analyzer = new FrameAnalyzer();
					Frame frame = null;
					while((frame = analyzer.analyze(bufferChannel)) != null) {
						if(frame instanceof Aac) {
							Aac aac = (Aac)frame;
							counter += aac.getSampleNum();
							sampleRate = aac.getSampleRate();
							audioDataList.add(aac);
						}
					}
					break;
				case AUDIO_MPEG1:
//					break;
				default:
					throw new RuntimeException();
				}
			}
			catch(Exception e) {
			}
			finally {
				if(bufferChannel != null) {
					try {
						bufferChannel.close();
					}
					catch(Exception e) {}
					bufferChannel = null;
				}
			}
		}
		pesList.add(pes);*/
		sampleRate = data.getSampleRate();
		counter += data.getSampleNum();
		audioDataList.add(data);
	}
	/**
	 * 終端pts値を参照する
	 * @return
	 */
	public long getLastDataPts() {
		return startPos + (long)(counter * (90000D / sampleRate));
	}
	/**
	 * 現在保持している先頭データpts値
	 * @return
	 */
	public long getFirstDataPts() {
		return startPos + (long)(sendedCounter * (90000D / sampleRate));
	}
	/**
	 * 先頭データを取り出す
	 * @return
	 */
	public IAudioData shift() {
		if(audioDataList.size() == 0) {
			return null;
		}
		IAudioData audioData = audioDataList.remove(0);
		sendedCounter += audioData.getSampleNum();
		return audioData;
	}
	/**
	 * 先頭にデータを戻す
	 * @param audioData
	 */
	public void unshift(IAudioData audioData) {
		if(audioData == null) {
			return;
		}
		sendedCounter -= audioData.getSampleNum();
		audioDataList.add(0, audioData);
	}
}
