package com.ttProject.transcode.ffmpeg.track;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITrackListener;

/**
 * ffmpegのtrackManagerの動作定義
 * @author taktod
 *
 */
public class FfmpegTrackManager implements IFfmpegTrackManager {
	/** 動作ID */
	private final int id;
	/** 出来上がったデータを参照するlistener */
	private ITrackListener trackListener = null;
	/** 変換マネージャー(例外がでたときに、通知したかったんだが・・・必要ないかな・・・) */
//	private final FfmpegTranscodeManager transcodeManager;
	/** unit選択プログラム */
	private IUnitSelector unitSelector = null;
	private List<Unit> units;
	/**
	 * コンストラクタ
	 * @param id
	 */
	public FfmpegTrackManager(/*FfmpegTranscodeManager transcodeManager,*/ int id) {
//		this.transcodeManager = transcodeManager;
		this.id = id;
	}
	/**
	 * id参照
	 */
	@Override
	public int getId() {
		return id;
	}
	/**
	 * データ参照リスナー設定
	 */
	@Override
	public void setTrackListener(ITrackListener listener) {
		this.trackListener = listener;
	}
	/**
	 * unit選択動作を設定します。
	 */
	@Override
	public void setUnitSelector(IUnitSelector selector) {
		this.unitSelector = selector;
	}
	/**
	 * データを登録する
	 * @param unit
	 */
	public void applyData(Unit unit) {
		if(!unitSelector.check(unit)) {
			return;
		}
		if(units == null) {
			units = new ArrayList<Unit>();
		}
		units.add(unit);
	}
	/**
	 * データを送信する
	 */
	public void commit() {
		trackListener.receiveData(units);
		units = null;
	}
}
