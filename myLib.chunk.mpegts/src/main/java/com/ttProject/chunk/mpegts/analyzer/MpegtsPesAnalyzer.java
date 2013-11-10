package com.ttProject.chunk.mpegts.analyzer;

import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.chunk.mpegts.AudioDataList;
import com.ttProject.chunk.mpegts.VideoDataList;
import com.ttProject.media.IAudioData;
import com.ttProject.media.Unit;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pmt;

/**
 * pesの内容から、必要な情報を抜き出してvideoDataListやaudioDataListに設置していくアナライザー
 * @author taktod
 */
public class MpegtsPesAnalyzer implements IPesAnalyzer {
	/** 動作ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MpegtsPesAnalyzer.class);
	private Pmt pmt;
	private VideoDataList videoDataList;
	private AudioDataList audioDataList;
	/** audioDataの解析オブジェクト */
	private IAudioDataAnalyzer audioDataAnalyzer = null;
	/**
	 * audioDataListを設定します
	 */
	@Override
	public void setAudioDataList(AudioDataList audioDataList) {
		this.audioDataList = audioDataList;
	}
	/**
	 * videoDataListを設定します
	 */
	@Override
	public void setVideoDataList(VideoDataList videoDataList) {
		this.videoDataList = videoDataList;
	}
	/**
	 * pesについて調査しなければいけない。
	 * 解析できたら、必要に応じてaudioDataListやvideoDataListにみつけたデータをいれなければならない。
	 */
	@Override
	public void analyze(Unit unit) {
		if(unit instanceof Pmt) {
			if(pmt == null) {
				this.pmt = (Pmt)unit;
			}
			return;
		}
		if(!(unit instanceof Pes)) {
			// pesのみに興味あり
			return;
		}
		Pes pes = (Pes)unit;
		if(pes.getPid() == videoDataList.getPid()) {
			// 動画のpesの場合
			videoDataList.addPes(pes);
		}
		else if(pes.getPid() == audioDataList.getPid()) {
			if(audioDataAnalyzer == null) {
				// 音声のpesの場合
				switch(pes.getCodec()) {
				case AUDIO_AAC:
					// aacの場合
					audioDataAnalyzer = new AacAudioDataAnalyzer();
					break;
				case AUDIO_MPEG1:
					// mp3の場合
					break;
				default:
					break;
				}
			}
			List<IAudioData> audioList = audioDataAnalyzer.analyzeAudioData(pes);
			if(audioList != null) {
				for(IAudioData audioData : audioList) {
					audioDataList.addAudioData(audioData, audioDataAnalyzer.getLastPtsValue());
				}
			}
		}
		return;
	}
}
