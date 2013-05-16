package com.ttProject.xuggle;

import java.util.Map;

import com.xuggle.xuggler.ISimpleMediaFile;
import com.xuggle.xuggler.IStreamCoder.Flags;

/**
 * 入力出力ともに、このインターフェイスを実装することにします。
 * @author taktod
 *
 */
public interface IMediaManager {
	/**
	 * 動作プロトコル
	 * @return
	 */
	public String getProtocol();
	/**
	 * 動作フォーマット
	 * @return
	 */
	public String getFormat();
	/**
	 * 出力ストリームの設定
	 * @return
	 */
	public ISimpleMediaFile getStreamInfo();
	/**
	 * 出力ビデオ詳細プロパティ
	 * @return
	 */
	public Map<String, String> getVideoProperty();
	/**
	 * 出力ビデオ詳細フラグ
	 * @return
	 */
	public Map<Flags, Boolean> getVideoFlags();
}
