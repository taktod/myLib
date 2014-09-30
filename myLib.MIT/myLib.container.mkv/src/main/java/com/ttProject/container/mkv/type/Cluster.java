/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.unit.UnitComparator;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Clusterタグ
 * @author taktod
 */
public class Cluster extends MkvMasterTag {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Cluster.class);
	/** このクラスタの時間上でのサイズ */
	private long duration;
	/** 処理中のtrackIdのリスト */
	private Set<Integer> trackIdSet = new HashSet<Integer>();
	/** 保持しているblockリスト */
	private List<SimpleBlock> blockList = new ArrayList<SimpleBlock>();
	/** ソート用のクラス */
	private static UnitComparator comparator = new UnitComparator();
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
	 * 時間に関するデータをセットアップする
	 * @param pts
	 * @param timebase
	 * @param duration
	 * @throws Exception
	 */
	public void setupTimeinfo(long pts, long timebase, long duration) throws Exception {
		setPts(pts);
		setTimebase(timebase);
		this.duration = duration;
		Timecode timecode = new Timecode();
		timecode.setValue(pts);
		addChild(timecode);
	}
	public void checkTrackId(int trackId) {
		trackIdSet.add(trackId);
	}
	/**
	 * 保持するフレームを追加します
	 * @param trackId
	 * @param frame
	 * @return IFrame 追加されなかったらframeを応答します。追加されたらnullを応答します。
	 */
	public IFrame addFrame(int trackId, IFrame frame) throws Exception {
		// TODO 登録されるであろうトラックがくる前にコンプリート扱いになることがあるみたいです。これはこまりますね。
//		trackIdSet.add(trackId);
		// このデータがcluster内のsimpleBlockになります。
		// 追加していくけど、次のclusterが来たときに、実は次のclusterにいれるべきデータがでてくるかもしれないので注意が必要
		int pts = (int)(getTimebase() * frame.getPts() / frame.getTimebase() - getPts());
//		logger.info(pts);
		if(pts <= 0) {
			return null;
		}
		if(pts >= 0 && pts < duration) {
//			logger.info("data for this cluster:" + getPts() + " " + frame);
			// 内部に入るデータ
			setupSimpleBlock(trackId, frame, pts);
			return null;
		}
//		logger.info("not for this cluster:" + getPts() + " " + frame);
		trackIdSet.remove((Integer)trackId);
		return frame;
	}
	/**
	 * ブロック化して保持しておく
	 * @param trackId
	 * @param frame
	 * @throws Exception
	 */
	private void setupSimpleBlock(int trackId, IFrame frame, int clusterPts) throws Exception {
		switch(frame.getCodecType()) {
		case H264:
			// h264の場合はsliceFrameのみ扱う
			if(!(frame instanceof SliceFrame)) {
				return;
			}
			break;
		default:
			break;
		}
		SimpleBlock simpleBlock = new SimpleBlock();
		simpleBlock.addFrame(trackId, frame, clusterPts);
		blockList.add(simpleBlock);
	}
	/**
	 * リストが空であるか判定する
	 * @return
	 */
	public boolean isCompleteCluster() {
		return trackIdSet.isEmpty();
	}
	/**
	 * 完了したclusterのデータを構築しておく。
	 */
	public void setupComplete() {
		// ソートする
		Collections.sort(blockList, comparator);
		for(MkvBlockTag blockTag : blockList) {
			addChild(blockTag);
		}
		blockList.clear();
	}
}
