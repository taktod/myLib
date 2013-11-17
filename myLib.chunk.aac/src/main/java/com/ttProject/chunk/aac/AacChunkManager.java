package com.ttProject.chunk.aac;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.MediaChunkManager;
import com.ttProject.chunk.aac.analyzer.IAacFrameAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.media.aac.frame.Aac;

/**
 * aacのchunkを取り出すための動作マネージャー
 * 基本的にgetChunksにunitデータをいれると対応したMediaChunkが取得できる感じ
 * @author taktod
 */
public class AacChunkManager extends MediaChunkManager{
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AacChunkManager.class);
	/** 処理中のmp3Data保持オブジェクト */
	private AacDataList aacDataList = new AacDataList();
	/** 解析用のオブジェクト */
	private Set<IAacFrameAnalyzer> analyzers = new HashSet<IAacFrameAnalyzer>();
	/** 経過frame数 */
	private long passedFrame = 0;
	/**
	 * aacの解析オブジェクトを登録する。
	 */
	public void addAacFrameAnalyzer(IAacFrameAnalyzer frameAnalyzer) {
		frameAnalyzer.setAacDataList(aacDataList);
		analyzers.add(frameAnalyzer);
	}
	/**
	 * chunkを取り出します
	 */
	@Override
	public IMediaChunk getChunk(Unit unit) throws Exception {
		// 解析してmp3DataListにデータを追記させる。
		for(IAacFrameAnalyzer analyzer : analyzers) {
			analyzer.analyze(unit);
		}
		// 完了したデータを確認してある場合はchunkを応答する
		return checkCompleteChunk();
	}
	/**
	 * 完了したchunkを確認。ある場合は応答します
	 * @return
	 * @throws Exception
	 */
	private IMediaChunk checkCompleteChunk() throws Exception {
		int sampleRate = aacDataList.getSampleRate();
		if(sampleRate == -1) { // 解析前の場合は処理しない
			return null;
		}
		// 処理したいframe数
		long targetFrameCount = passedFrame + (long)(getDuration() * sampleRate);
		if(aacDataList.getCounter() > targetFrameCount) {
			/** 現在処理中のchunkオブジェクト */
			AacChunk chunk = null;
			if(chunk == null) {
				chunk = new AacChunk(sampleRate);
				chunk.setTimestamp(aacDataList.getFirstCounter());
			}
			// データを構築する。
			int frameCount = 0;
			do {
				Aac frame = aacDataList.shift();
				frameCount += frame.getSampleNum();
				chunk.write(frame.getBuffer());
			} while(aacDataList.getFirstCounter() <= targetFrameCount);
			chunk.setDuration(1.0f * frameCount / sampleRate);
			passedFrame += frameCount;
			return chunk;
		}
		return null;
	}
	/**
	 * 処理中のchunkの参照(chunkの処理中はありません)
	 */
	@Override
	public IMediaChunk getCurrentChunk() {
		return null;
	}
	/**
	 * 残りデータがある場合はここで応答しないとだめ。(でないとVODできちんとした長さのデータにならなくなる。(欠損がでる))
	 */
	@Override
	public IMediaChunk close() {
		return null;
	}
	/**
	 * 拡張子応答
	 */
	@Override
	public String getExt() {
		return "mp3";
	}
	/**
	 * mp3の分割では、headerという概念が存在しない
	 */
	@Override
	@Deprecated
	public String getHeaderExt() {
		return "mp3";
	}
	/**
	 * 経過時刻を応答します
	 */
	@Override
	public long getPassedTic() {
		return passedFrame;
	}
	/**
	 * 動作サンプルレートを応答します。
	 * @return 処理前は-1が応答されます。
	 */
	public int getSampleRate() {
		return aacDataList.getSampleRate();
	}
}
