package com.ttProject.chunk.mpegts;

import java.util.List;

import com.ttProject.chunk.IMediaChunk;
import com.ttProject.chunk.MediaChunkManager;
import com.ttProject.media.Unit;

/**
 * mpegtsのchunkを取り出すための動作マネージャー
 * 基本的にgetChunksにUnitデータ(flvのTagとかmpegtsのPacketとか)をいれると、対応したMediaChunkがでてくる。
 */
public class MpegtsChunkManager extends MediaChunkManager {
	@Override
	public List<IMediaChunk> getChunks(Unit unit) {
		return null;
	}
	@Override
	public IMediaChunk getCurrentChunk() {
		return null;
	}
	@Override
	public String getExt() {
		return null;
	}
	@Override
	public String getHeaderExt() {
		return null;
	}
	@Override
	public float getDuration() {
		return 0;
	}
}
