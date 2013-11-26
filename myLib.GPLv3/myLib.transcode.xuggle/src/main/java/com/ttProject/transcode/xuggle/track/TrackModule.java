package com.ttProject.transcode.xuggle.track;

/**
 * track動作のモジュール
 * @author taktod
 */
public abstract class TrackModule {
	/**
	 * オブジェクトの確認をあらかじめ実行します。
	 * (thread処理に入る前に分岐させるため)
	 * @param xuggleObject
	 * @return true:処理するオブジェクト false:処理しないオブジェクト
	 */
	protected abstract boolean checkObject(Object xuggleObject);
	/**
	 * トラック用のエンコード処理を進める動作
	 * @param xuggleObject
	 */
	protected abstract void process(Object xuggleObject);
	/**
	 * 終了処理
	 */
	protected abstract void close();
}
