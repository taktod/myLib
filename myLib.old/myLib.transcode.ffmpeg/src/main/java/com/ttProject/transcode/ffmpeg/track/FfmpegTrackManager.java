/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg.track;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITrackListener;

/**
 * ffmpegのtrackManagerの動作定義
 * @author taktod
 */
public class FfmpegTrackManager implements IFfmpegTrackManager {
	/** 動作ID */
	private final int id;
	/** 出来上がったデータを参照するlistener */
	private ITrackListener trackListener = null;
	/** unit選択プログラム */
	private IUnitSelector unitSelector = null;
	/** 処理unit */
	private List<Unit> units;
	/**
	 * コンストラクタ
	 * @param id
	 */
	public FfmpegTrackManager(int id) {
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
	/**
	 * 停止処理
	 */
	public void close() {
		if(units != null) {
			units.clear();
			units = null;
		}
		if(unitSelector != null) {
			unitSelector.close();
			unitSelector = null;
		}
		if(trackListener != null) {
			trackListener.close();
			trackListener = null;
		}
	}
}
