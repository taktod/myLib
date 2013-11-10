package com.ttProject.chunk.mpegts;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.mpegts.field.AdaptationField;
import com.ttProject.media.mpegts.packet.Pes;

/**
 * videoDataは簡単に移動ができないので(keyFrameの位置をずらしたりする場合は再変換しなければいけないため)
 * pesベースで取り込んで持っておいてOK
 */
public class VideoDataList extends MediaDataList {
	/** ロガー */
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(VideoDataList.class);
	/** 映像のpesデータリスト */
	private final List<Pes> videoPesList = new ArrayList<Pes>();
	/** 映像のキーフレームの先頭の部分のpesリスト */
	private final List<Pes> keyPesList = new ArrayList<Pes>();
	/**
	 * pesデータを追記する
	 * @param pes
	 */
	public void addPes(Pes pes) {
		if(getPid() != pes.getPid() // 自分のデータではない
			|| getCodecType() == null) { // 初期化が済んでいない
			return; // スキップしておく。
		}
		// 新規データなのでとりあえず追加しておく。
		videoPesList.add(pes);
		if(pes.isPayloadUnitStart()) { // データpacketの開始位置の場合
			AdaptationField field = pes.getAdaptationField();
			if(pes.isAdaptationFieldExist() && field != null) { // adaptationFieldが存在している場合
				if(field.getRandomAccessIndicator() == 1) { // ランダムアクセスが許可されているデータの場合(キーフレームであるということ)
					keyPesList.add(pes); // キーフレームのpesデータなので、リストにいれておく。
				}
			}
		}
	}
	/**
	 * 終端データのpts値
	 * @return
	 */
	public long getLastDataPts() {
		if(keyPesList.size() == 0) {
			return -1;
		}
		Pes lastKeyFramePes = keyPesList.get(keyPesList.size() - 1);
		return lastKeyFramePes.getPts().getPts();
	}
	/**
	 * 先頭データのpts値
	 */
	public long getFirstDataPts() {
		if(keyPesList.size() == 0) {
			return -1;
		}
		Pes firstKeyFramePes = keyPesList.get(0);
		return firstKeyFramePes.getPts().getPts();
	}
	/**
	 * 先頭を取り出す
	 * @return
	 */
	public Pes shift() {
		if(videoPesList.size() == 0) {
			return null;
		}
		// TODO keyPesと一致する場合はそっちも抜いておくべき。
		return videoPesList.remove(0);
	}
	/**
	 * 先頭に追加
	 * データを取り出したけど、使わなかったときに戻す感じ
	 */
	public void unshift(Pes pes) {
		if(pes == null) {
			return;
		}
		videoPesList.add(0, pes);
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("videoDataList:").append(videoPesList.size());
		return data.toString();
	}
}
