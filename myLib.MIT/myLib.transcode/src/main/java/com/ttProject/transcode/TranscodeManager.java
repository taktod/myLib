package com.ttProject.transcode;

public abstract class TranscodeManager implements ITranscodeManager {
	/** 出力の設定先 */
	private ITranscodeListener listener;
	/**
	 * 出力先参照
	 * @return
	 */
	protected ITranscodeListener getTranscodeListener() {
		return listener;
	}
	/**
	 * 出力取得用listener設定
	 * @param listener
	 */
	@Override
	public void addTranscodeListener(ITranscodeListener listener) {
		this.listener = listener;
	}
}
