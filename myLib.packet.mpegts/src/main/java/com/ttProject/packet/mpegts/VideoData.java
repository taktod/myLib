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
	private final Logger logger = Logger.getLogger(VideoData.class);
	private final List<Pes> videoPesList = new ArrayList<Pes>();
	private final List<Pes> keyPesList = new ArrayList<Pes>();
	/**
	 * pesデータを解析します。
	 */
	@Override
	public void analyzePes(Pes pes) {
		if(!checkPes(pes)) {
			return;
		}
		// 自分用のデータなので対処しておく。
//		logger.info("動画データ解析");
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
	// すすんだ時間データを応答する。
	// データを取り出す。
}
