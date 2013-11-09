package com.ttProject.chunk.mpegts;

import org.apache.log4j.Logger;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.MediaChunkManager;
import com.ttProject.media.Unit;
import com.ttProject.media.mpegts.Packet;

/**
 * mpegtsのchunkを取り出すための動作マネージャー
 * 基本的にgetChunksにUnitデータ(flvのTagとかmpegtsのPacketとか)をいれると、対応したMediaChunkがでてくる。
 */
public class MpegtsChunkManager extends MediaChunkManager {
	private Logger logger = Logger.getLogger(MpegtsChunkManager.class);
	/**
	 * chunkを取り出します。
	 */
	@Override
	public IMediaChunk getChunk(Unit unit) {
		// 映像データの場合は、mpegtsのpesに変更しなければいけない。
		// 音声データの場合は、IAudioDataに変更しなければいけない。
		return null;
	}
	/**
	 * 現在処理中のchunkについて応答する。
	 */
	@Override
	public IMediaChunk getCurrentChunk() {
		return null;
	}
	/**
	 * 残りデータがある場合はここで応答しなければいけない。
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
		return "ts";
	}
	/**
	 * mpegtsの分割では、headerという概念が存在しない
	 */
	@Override
	@Deprecated
	public String getHeaderExt() {
		return "ts";
	}
	/**
	 * 処理時間(秒数表記)
	 */
	@Override
	public float getDuration() {
		return 0;
	}
}
