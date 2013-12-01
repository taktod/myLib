package com.ttProject.chunk.mp3;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.MediaChunkManager;
import com.ttProject.chunk.mp3.analyzer.IMp3FrameAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.media.mp3.frame.Mp3;

/**
 * mp3のchunkを取り出すための動作マネージャー
 * 基本的にgetChunksにUnitデータ(flvのTagとかmpegtsのPacketとか)をいれると
 * 対応したMediaChunkがでてくる。
 * @author taktod
 */
public class Mp3ChunkManager extends MediaChunkManager {
	/** ロガー */
	private Logger logger = Logger.getLogger(Mp3ChunkManager.class);
	/** 処理中のmp3Data保持オブジェクト */
	private Mp3DataList mp3DataList = new Mp3DataList();
	/** 解析用のオブジェクト */
	private Set<IMp3FrameAnalyzer> analyzers = new HashSet<IMp3FrameAnalyzer>();
	/** 経過frame数 */
	private long passedFrame = 0;
	/**
	 * mp3の解析オブジェクトを登録する。
	 */
	public void addMp3FrameAnalyzer(IMp3FrameAnalyzer frameAnalyzer) {
		frameAnalyzer.setMp3DataList(mp3DataList);
		analyzers.add(frameAnalyzer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMediaChunk getChunk(Unit unit) throws Exception {
		// 解析してmp3DataListにデータを追記させる。
		for(IMp3FrameAnalyzer analyzer : analyzers) {
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
		int sampleRate = mp3DataList.getSampleRate();
		if(sampleRate == -1) { // 解析前の場合は処理しない
			return null;
		}
		// 処理したいframe数
		long targetFrameCount = passedFrame + (long)(getDuration() * sampleRate);
		if(mp3DataList.getCounter() > targetFrameCount) {
			/** 現在処理中のchunkオブジェクト */
			Mp3Chunk chunk = new Mp3Chunk(sampleRate);
			chunk.setTimestamp(mp3DataList.getFirstCounter());
			// データを構築する。
			int frameCount = 0;
			do {
				Mp3 frame = mp3DataList.shift();
				frameCount += frame.getSampleNum();
				chunk.write(frame.getBuffer());
			} while(mp3DataList.getFirstCounter() <= targetFrameCount);
			chunk.setDuration(1.0f * frameCount / sampleRate);
			passedFrame += frameCount;
			return chunk;
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMediaChunk getCurrentChunk() {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMediaChunk close() {
		if(mp3DataList.getListCount() == 0) {
			return null;
		}
		// 最終のときには、中身すべて吐き出しておく。
		try {
			int sampleRate = mp3DataList.getSampleRate();
			Mp3Chunk chunk = new Mp3Chunk(sampleRate);
			chunk.setTimestamp(mp3DataList.getFirstCounter());
			int frameCount = 0;
			Mp3 frame = null;
			while((frame = mp3DataList.shift()) != null) {
				frameCount += frame.getSampleNum();
				chunk.write(frame.getBuffer());
			}
			chunk.setDuration(1.0f * frameCount / sampleRate);
			passedFrame += frameCount;
			return chunk;
		}
		catch(Exception e) {
			logger.warn("エラー", e);
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExt() {
		return "mp3";
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public String getHeaderExt() {
		return "mp3";
	}
	/**
	 * {@inheritDoc}
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
		return mp3DataList.getSampleRate();
	}
}
