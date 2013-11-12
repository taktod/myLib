package com.ttProject.chunk.mpegts;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.IAudioData;

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
	private long startPos = -1;
	/** sampleRate値(時間の計算で必要) */
	private int sampleRate = 44100;
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
	public void addAudioData(IAudioData data, long pts) {
		if(startPos == -1 && pts != -1L) {
			startPos = pts;
		}
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
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("audioDataList:").append(audioDataList.size());
		return data.toString();
	}
}
