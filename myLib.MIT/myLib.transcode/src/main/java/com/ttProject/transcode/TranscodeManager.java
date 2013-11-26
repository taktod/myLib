package com.ttProject.transcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 変換動作の主体となる部分の共通動作抜き出し
 * とりあえずこれが根幹
 * @author taktod
 */
public abstract class TranscodeManager implements ITranscodeManager {
	/** 例外捕捉 */
	private IExceptionListener expListener = null;
	/** 保持trackManagerリスト */
	private Map<Integer, ITrackManager> trackManagers = new HashMap<Integer, ITrackManager>();
	/** managerのID候補 */
	private int nextId = 1;
	/**
	 * 例外捕捉
	 */
	@Override
	public void addExceptionListener(IExceptionListener listener) {
		expListener = listener;
	}
	/**
	 * 例外発行
	 * @param e
	 */
	protected void reportException(Exception e) {
		if(expListener != null) {
			expListener.exceptionCaught(e);
		}
		else {
			throw new RuntimeException(e);
		}
	}
	/**
	 * trackManagerを取得します
	 * @return
	 */
	@Override
	public ITrackManager getTrackManager(int id) {
		if(!trackManagers.containsKey(id)) {
			return null;
		}
		return trackManagers.get(id);
	}
	/**
	 * 新しいtrackManagerを生成します
	 * @return
	 * @throws Exception
	 */
	@Override
	public ITrackManager addNewTrackManager() throws Exception {
		nextId ++;
		ITrackManager trackManager = makeTrackManager(nextId);
		if(trackManager == null) {
			throw new Exception("trackを追加しようとしたら、エラーが発生しました。");
		}
		trackManagers.put(trackManager.getId(), trackManager);
		return trackManager;
	}
	/**
	 * 各TranscodeManager用のtrackManagerを作成する動作
	 * @param newId
	 * @return
	 */
	protected abstract ITrackManager makeTrackManager(int newId);
	/**
	 * trackManager全体を参照する
	 * @return
	 */
	protected Map<Integer, ITrackManager> getTrackManagers() {
		return trackManagers;
	}
}
