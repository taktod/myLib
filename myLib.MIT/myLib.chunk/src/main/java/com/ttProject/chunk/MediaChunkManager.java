package com.ttProject.chunk;

/**
 * mediaChunkManagerの共通処理を一本化
 * @author taktod
 */
public abstract class MediaChunkManager implements IMediaChunkManager {
	/** 内部で設定されているduration値 */
	private float duration = 2;
	/**
	 * {@inheritDoc}
	 * 秒数経過
	 */
	@Override
	public float getDuration() {
		return duration;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDuration(float duration) {
		this.duration = duration;
	}
}
