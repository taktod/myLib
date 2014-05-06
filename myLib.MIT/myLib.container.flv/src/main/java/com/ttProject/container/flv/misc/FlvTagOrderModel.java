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
	private final Logger logger = Logger.getLogger(FlvTagOrderModel.class);
	/** 遅延の許容量設定(ミリ秒単位) */
	private final long delayTime = 1000L;
	private final List<AudioTag> audioTags = new ArrayList<AudioTag>();
	private final List<VideoTag> videoTags = new ArrayList<VideoTag>();
	private long passedPts = -1; // うけとり済みptsの一番あたらしいもの
	private final TagComparator tagSort = new TagComparator();
	private long nextAudioPts = -1; // audioPtsとして連続していると判定する範囲値(この値以内のptsは即確定できる)
	private long processedPts = -1; // すでに変換にまわしたpts値これ以前のpts値のデータをうけとってももう利用できない
	
	public void reset() {
		audioTags.clear();
		videoTags.clear();
	}
	public void addTag(FlvTag tag) throws Exception {
		if(tag instanceof AggregateTag) {
			AggregateTag aTag = (AggregateTag) tag;
			for(FlvTag ftag : aTag.getList()) {
				addTag(ftag);
			}
			return;
		}
		if(processedPts > tag.getPts()) {
			// すでに処理確定済みのptsのデータをうけとりました。
			// 利用できません。
			return;
		}
		if(passedPts < tag.getPts()) {
			passedPts = tag.getPts();
		}
		if(tag instanceof AudioTag) {
			logger.info("audio:" + tag);
			audioTags.add((AudioTag)tag);
			Collections.sort(audioTags, tagSort);
		}
		else if(tag instanceof VideoTag) {
			logger.info("video:" + tag);
			videoTags.add((VideoTag)tag);
			Collections.sort(videoTags, tagSort);
		}
	}
	public List<FlvTag> getCompleteTag() throws Exception {
		List<FlvTag> result = new ArrayList<FlvTag>();
		VideoTag videoTag = null;
		AudioTag audioTag = null;
		while(true) {
			// 映像の確定データについて調べる
			videoTag = getComfirmedVideoTag();
			// 音声の確定データについて調べる
			audioTag = getComfirmedAudioTag();
			
			if(videoTag.getPts() > audioTag.getPts()) {
				
			}
			// audioとvideoのデータから一番若いデータをベースに処理をすすめる必要あり。
			break;
		}
		return result;
	}
	/**
	 * 確定済みのvideoTagで一番若いものを応答する
	 * @return
	 */
	private VideoTag getComfirmedVideoTag() throws Exception {
		if(videoTags.size() > 2) {
			// タグのデータが0か1の場合は確定タグがない
			return videoTags.get(0); // 先頭のデータが確定データで一番わかいデータになる。
		}
		else {
			return null;
		}
	}
	private AudioTag getComfirmedAudioTag() throws Exception {
		AudioTag result = null;
		if(nextAudioPts == -1) {
			// audioPtsが決定していない場合は先頭のデータが確実に必要な応答データになるので、それを応答する。
			result = audioTags.get(0);
		}
		else { // nextAudioPtsが-1でない場合は通常の動作
			// pts値が規程範囲内でない場合は・・・どうしようかね？
			AudioTag target = audioTags.get(0);
			if(nextAudioPts > target.getPts()) { // 範囲内の場合は確定させる
				result = target;
			}
			else {
				// 範囲外の場合は確定させることができない。
				logger.error("致命的なことがなんかおこった。");
			}
		}
		nextAudioPts = result.getPts() + (2000L * result.getSampleNum() / result.getSampleRate());
		return result;
	}
	private class TagComparator implements Comparator<FlvTag> {
		@Override
		public int compare(FlvTag tag1, FlvTag tag2) {
			return (int)(tag1.getPts() - tag2.getPts());
		}
	}
}
