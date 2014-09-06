/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IFrame;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Clusterタグ
 * @author taktod
 */
public class Cluster extends MkvMasterTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Cluster.class);
	/** このクラスタの時間上でのサイズ */
	private long duration;
	/** 処理中のtrackIdのリスト */
	private Set<Integer> trackIdSet = new HashSet<Integer>();
	/**
	 * コンストラクタ
	 * @param size
	 */
	public Cluster(EbmlValue size) {
		super(Type.Cluster, size);
	}
	/**
	 * コンストラクタ
	 */
	public Cluster() {
		this(new EbmlValue());
	}
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Cluster(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * 位置を設定する
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPts(long pts) {
		// このptsがtimecodeに影響を与えるものとします
		super.setPts(pts);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimebase(long timebase) {
		super.setTimebase(timebase);
	}
	/**
	 * データの長さのduration値
	 * @param duration
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	/**
	 * 保持するフレームを追加します
	 * @param trackId
	 * @param frame
	 * @return IFrame 追加されなかったらframeを応答します。追加されたらnullを応答します。
	 */
	public IFrame addFrame(int trackId, IFrame frame) {
		trackIdSet.add(trackId);
		// このデータがcluster内のsimpleBlockになります。
		// 追加していくけど、次のclusterが来たときに、実は次のclusterにいれるべきデータがでてくるかもしれないので注意が必要
		long pts = getTimebase() * frame.getPts() / frame.getTimebase() - getPts();
		if(pts >= 0 && pts < duration) {
			// 内部に入るデータ
			return null;
		}
		trackIdSet.remove((Integer)trackId);
		return frame;
	}
	/**
	 * リストが空であるか判定する
	 * @return
	 */
	public boolean isCompleteCluster() {
		return trackIdSet.isEmpty();
	}
}
