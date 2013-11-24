package com.ttProject.transcode;

public abstract class TranscodeManager implements ITranscodeManager {
	/** 例外捕捉 */
	private IExceptionListener expListener = null;
	/**
	 * 例外捕捉
	 */
	@Override
	public void addExceptionListener(IExceptionListener listener) {
		expListener = listener;
	}
	/**
	 * 例外リスナー参照
	 * @return
	 */
	protected IExceptionListener getExpListener() {
		return expListener;
	}
}
