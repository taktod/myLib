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
	/** 最終pesデータのpts値記録 */
	private long lastDataPts = -1L;
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
		if(pes.hasPts() && lastDataPts < pes.getPts().getPts()) {
			lastDataPts = pes.getPts().getPts();
		}
	}
	/**
	 * 終端データのpts値
	 * @return
	 */
	public long getLastDataPts() {
		if(keyPesList.size() == 0) {
			return lastDataPts;
		}
		Pes lastKeyFramePes = keyPesList.get(keyPesList.size() - 1);
		return lastKeyFramePes.getPts().getPts();
	}
	/**
	 * 先頭データのpts値
	 */
	public long getFirstDataPts() {
		if(keyPesList.size() == 0) {
			return lastDataPts;
		}
		Pes firstKeyFramePes = keyPesList.get(0);
		return firstKeyFramePes.getPts().getPts();
	}
	public long getSecondDataPts() {
		if(keyPesList.size() <= 1) {
			return -1;
		}
		Pes secondKeyFramePes = keyPesList.get(1);
		return secondKeyFramePes.getPts().getPts();
	}
	/**
	 * 先頭を取り出す
	 * @return
	 */
	public Pes shift() {
		if(videoPesList.size() == 0) {
			return null;
		}
		Pes targetPes = videoPesList.remove(0);
		// keyPesと一致するpesがある場合は撤去しておく
		if(keyPesList.contains(targetPes)) {
			keyPesList.remove(targetPes);
		}
		return targetPes;
	}
	/**
	 * 先頭に追加
	 * データを取り出したけど、使わなかったときに戻す感じ
	 */
	public void unshift(Pes pes) {
		if(pes == null) {
			return;
		}
		// 相手がkeyFrameとなりうるpesの場合は・・・keyFrame側にも追加する必要がある。
		videoPesList.add(0, pes);
		if(pes.isPayloadUnitStart()) { // データpacketの開始位置の場合
			AdaptationField field = pes.getAdaptationField();
			if(pes.isAdaptationFieldExist() && field != null) { // adaptationFieldが存在している場合
				if(field.getRandomAccessIndicator() == 1) { // ランダムアクセスが許可されているデータの場合(キーフレームであるということ)
					keyPesList.add(0, pes); // キーフレームのpesデータなので、リストにいれておく。
				}
			}
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("videoDataList:").append(videoPesList.size());
		return data.toString();
	}
}
