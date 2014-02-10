package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * TrackEntryタグ
 * ここからデータを拾えるようにだけ調整しておきたいね。
 * @author taktod
 */
public class TrackEntry extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public TrackEntry(EbmlValue size) {
		super(Type.TrackEntry, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * load後に、扱いやすいようにデータを設定しておきます。
	 * @param defaultTimebase
	 * @return trackIdを応答します。(uintですが、そこまで大きな数字になることはほぼないと思うのでintegerにまるめます。)
	 */
	public int setupEntry(long defaultTimebase) throws Exception {
		TrackUID trackUid = null;
		if(trackUid == null) {
			throw new Exception("trackUidが見つかりませんでした。");
		}
		return (int)trackUid.getValue();
	}
}
