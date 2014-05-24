/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.packet.mpegts.nw;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.IAudioData;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 音声データ保持オブジェクト
 * 音声データは、映像データと完全に同期させるのが難しいので、いったんpesからaacもしくはmp3のframeに分解後再構築させることにします。
 * @author taktod
 */
public class AudioData extends MediaData {
	/** ロガー */
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AudioData.class);
	/** 分解前のpesデータリスト(1パケット分だけ保持する形) */
	private final List<Pes> pesList = new ArrayList<Pes>();
	/** 分解後のIAudioDataリスト */
	private final List<IAudioData> audioDataList = new ArrayList<IAudioData>();
	/** 経過サンプル数 */
	private long counter = 0;
	/** 転送済みのサンプル数 */
	private long sendedCounter = 0;
	/** データ開始位置のpts */
	private long startPos = -1;
	/** データのサンプルレート */
	private int sampleRate = 44100;
	/**
	 * 保持データリストのサイズを参照して確認したかった。
	 * @return
	 */
	public int getListCount() {
		return audioDataList.size();
	}
	/**
	 * pesの中身を解析します
	 */
	@Override
	public void analyzePes(Pes pes) {
		if(!checkPes(pes)) {
			return;
		}
		if(startPos == -1) {
			// 開始位置のpts値を保存しておく。(ずれ分)
			startPos = pes.getPts().getPts();
		}
		// 自分用のデータなので対処しておく。
		// 音声データのpesは分解して、aacもしくはmp3に変更してしまっておく。
		// adaptationFieldのrandomAccessIndicator
		if(pes.isPayloadUnitStart()) {
			ByteBuffer buffer = ByteBuffer.allocate(188 * pesList.size());
			while(pesList.size() > 0) {
				Pes p = pesList.remove(0);
				buffer.put(p.getRawData());
			}
			buffer.flip(); // aacもしくはmp3として分解できそうなデータはそろった。あとは分解して、配列に突っ込んでおくだけ。
			switch(getCodecType()) {
			case AUDIO_AAC: // aacの場合
				IReadChannel bufferChannel = null;
				try {
					bufferChannel = new ByteReadChannel(buffer);
					IFrameAnalyzer analyzer = new FrameAnalyzer();
					Frame frame = null;
					while((frame = analyzer.analyze(bufferChannel)) != null) {
						if(frame instanceof Aac) {
							Aac aac = (Aac)frame;
							counter += aac.getSampleNum();
							audioDataList.add(aac);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
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
				break;
			case AUDIO_MPEG1: // mp3の場合
/*				try {
					IFrameAnalyzer analyzer = new FrameAnalyzer();
					Frame frame = null;
					while((frame = analyzer.analyze(bufferChannel)) != null) {
						if(frame instanceof Aac) {
							Aac aac = (Aac)frame;
							counter += aac.getSampleNum();
							audioDataList.add(aac);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}*/
			default:
				throw new RuntimeException("扱うことができないmpegtsの音声コーデックです。");
			}
		}
		pesList.add(pes);
	}
	/**
	 * 先頭データを取り出す。
	 * @return
	 */
	public IAudioData shift() {
		if(audioDataList.size() == 0) 
			return null;
		IAudioData audioData = audioDataList.remove(0);
		sendedCounter += audioData.getSampleNum();
		return audioData;
	}
	/**
	 * 先頭にデータを追加
	 * @param audioData
	 */
	public void unshift(IAudioData audioData) {
		if(audioData == null) {
			return;
		}
		sendedCounter -= audioData.getSampleNum(); // 戻すサンプル数
		audioDataList.add(0, audioData);
	}
	/**
	 * 現在保持しているデータの終端pts値
	 * @return
	 */
	@Override
	public long getLastDataPts() {
		return startPos + (long)(counter * (90000D / sampleRate));
	}
	/**
	 * 現在保持しているデータの先頭pts値
	 * @return
	 */
	@Override
	public long getFirstDataPts() {
		return startPos + (long)(sendedCounter * (90000D / sampleRate));
	}
	/**
	 * ptsにしたときの現在転送済みのaudioデータ料を応答します。
	 * @return
	 */
	public long getSendedDataPts() {
		return startPos + (long)(sendedCounter * (90000D / 44100D));
	}
}
