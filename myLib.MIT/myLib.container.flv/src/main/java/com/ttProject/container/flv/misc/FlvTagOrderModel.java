/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.AggregateTag;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;

/**
 * ffmpegとかの変換にまわすデータをつくる動作
 * @author taktod
 * 映像はgapを考えずに、すでに処理済みtimestamp以前のデータがきた場合は、必要ないので捨てる必要あり。
 * 映像のデータの次のデータがきた場合は、処理しなくていいので、確定にまわしたい。
 * 
 * 音声の処理が進んでデータがぬけている場合は、変換を動作させるために、最終で利用したframeを挿入しておいとくことにする。
 * 
 * 音声の場合は、sampleNumから次のデータがくるべき位置がわかる
 * データがうまるように転送されてきた場合は、決定したものとして扱っておく。
 * 
 * 映像処理が進んでデータがぬけている場合は、変換を動作させるために、対応する無音frameを挿入しておくことにしておく。
 */
public class FlvTagOrderModel {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FlvTagOrderModel.class);
	/** ソート用比較オブジェクト */
	private static final FlvTagComparator comparator = new FlvTagComparator();
	private List<FlvTag> videoTags = new ArrayList<FlvTag>();
	private List<FlvTag> audioTags = new ArrayList<FlvTag>();
	/** 処理済みvideoTagのpts値 */
	private long passedVideoPts = -1;
	/** 処理済みaudioTagのpts値 */
	private long passedAudioPts = -1;
	/**
	 * 内部のデータをリセットする
	 * 主にunpublishしたときの処理
	 */
	public void reset() {
		videoTags.clear();
		audioTags.clear();
		passedVideoPts = -1;
		passedAudioPts = -1;
	}
	/**
	 * tagを追加する
	 */
	public void addTag(FlvTag tag) {
		if(tag instanceof AggregateTag) {
			AggregateTag aTag = (AggregateTag) tag;
			for(FlvTag fTag : aTag.getList()) {
				addTag(fTag);
			}
			return;
		}
		if(tag instanceof VideoTag) {
			if(tag.getPts() < passedVideoPts) {
				// 処理済みのtimestamp以前のデータなら捨てておく
				return;
			}
			videoTags.add(tag);
		}
		else if(tag instanceof AudioTag){
			if(tag.getPts() < passedAudioPts) {
				// 処理済みのtimestamp以前のデータなら捨てておく
				return;
			}
			audioTags.add(tag);
		}
	}
	/**
	 * 音声のソート済みデータを取得する
	 */
	public List<FlvTag> getAudioCompleteTag() {
		Collections.sort(audioTags, comparator);
		List<FlvTag> result = new ArrayList<FlvTag>();
		while(audioTags.size() > 1) {
			FlvTag tag = audioTags.remove(0);
			if(tag.getPts() < passedAudioPts) {
				continue;
			}
			passedAudioPts = tag.getPts();
			if(passedVideoPts < passedAudioPts - 1000) {
				passedVideoPts = passedAudioPts - 1000;
			}
			result.add(tag);
		}
		return result;
	}
	/**
	 * 映像のソート済みデータを取得する
	 */
	public List<FlvTag> getVideoCompleteTag() {
		Collections.sort(videoTags, comparator);
		List<FlvTag> result = new ArrayList<FlvTag>();
		while(videoTags.size() > 1) {
			FlvTag tag = videoTags.remove(0);
			if(tag.getPts() < passedVideoPts) {
				continue;
			}
			passedVideoPts = tag.getPts();
			if(passedAudioPts < passedVideoPts - 1000) {
				passedAudioPts = passedVideoPts - 1000;
			}
			result.add(tag);
		}
		return result;
	}
	/**
	 * tagの比較クラス
	 * @author taktod
	 */
	public static class FlvTagComparator implements Comparator<FlvTag> {
		@Override
		public int compare(FlvTag tag1, FlvTag tag2) {
			return (int)(tag1.getPts() - tag2.getPts());
		}
	}
}
