package com.ttProject.packet.mpegts;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.mpegts.field.AdaptationField;
import com.ttProject.media.mpegts.packet.Pes;

/**
 * 映像データ保持オブジェクト
 * このクラスに、Pesデータを投げ込んでいきます。
 * 取得すると、KeyFrameから次のkeyFrameまでのデータが応答されます。
 * @author taktod
 */
public class VideoData extends MediaData {
	/** ロガー */
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(VideoData.class);
	/** 映像のpesデータリスト */
	private final List<Pes> videoPesList = new ArrayList<Pes>();
	/** 映像のキーフレームのpesデータリスト(キーフレームの先頭の部分となります。) */
	private final List<Pes> keyPesList = new ArrayList<Pes>();
	/**
	 * pesデータを解析します。
	 */
	@Override
	public void analyzePes(Pes pes) {
		if(!checkPes(pes)) {
			return;
		}
		// pesデータはそのまま保持しておけばいいと思われる。
		// 順番にいれていくだけ、
		// ただしkeyFrameの位置だけは知っておく必要あり。
		videoPesList.add(pes);
		// データの開始位置であるか参照
		if(pes.isPayloadUnitStart()) {
			// データの開始位置の場合は、keyFrameかinnerFrameの始まりの部分
			AdaptationField field = pes.getAdaptationField();
			if(field != null) {
				// ランダムアクセスしていい場所である場合(要はアクセス開始位置(keyFrameとなる))
				if(field.getRandomAccessIndicator() == 1) {
					// キーフレームの位置となる。
					keyPesList.add(pes);
				}
			}
		}
	}
	/**
	 * 現在保持しているデータの終端pts値
	 * @return -1:存在しない。 数値:対象pts
	 */
	@Override
	public long getLastDataPts() {
		Pes lastKeyFramePes = keyPesList.get(keyPesList.size() - 1);
		if(lastKeyFramePes == null) {
			return -1;
		}
		return lastKeyFramePes.getPts().getPts();
	}
	/**
	 * 現在保持しているデータの先頭pts値
	 * @return -1:存在しない。 数値:対象pts
	 */
	@Override
	public long getFirstDataPts() {
		Pes firstKeyFramePes = keyPesList.get(0);
		if(firstKeyFramePes == null) {
			return -1;
		}
		return firstKeyFramePes.getPts().getPts();
	}
	/**
	 * 先頭を取り出す
	 * @return
	 */
	public Pes shift() {
		return videoPesList.remove(0);
	}
	/**
	 * 先頭に追加
	 * @param pes
	 */
	public void unshift(Pes pes) {
		videoPesList.add(0, pes);
	}
}
