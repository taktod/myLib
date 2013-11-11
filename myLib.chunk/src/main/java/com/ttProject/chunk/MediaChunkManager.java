package com.ttProject.chunk;

/**
 * mediaChunkManagerの共通処理を一本化したい。
 * @author taktod
 *
 */
public abstract class MediaChunkManager implements IMediaChunkManager {
	/** 内部で設定されているduration値 */
	private float duration = 2;
	/**
	 * 処理時間(秒数表記)
	 */
	@Override
	public float getDuration() {
		return duration;
	}
	/**
	 * chunkに設定されるべき時間
	 */
	@Override
	public void setDuration(float duration) {
		this.duration = duration;
	}
}
