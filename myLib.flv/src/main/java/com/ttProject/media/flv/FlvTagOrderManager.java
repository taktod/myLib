package com.ttProject.media.flv;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;

/**
 * Flvのtagのソートをおこなう
 * getTagsにデータをいれると、見合ったデータが返ってくる
 * audioとvideoのデータがflipしていてもいいが、audioだけ or videoだけでflipしていると扱えないものとする。
 * @author taktod
 */
public class FlvTagOrderManager {
	private final List<Tag> audioTags = new ArrayList<Tag>();
	private final List<Tag> videoTags = new ArrayList<Tag>();
	private boolean videoEndFlg = false;
	private boolean audioEndFlg = false;
	/**
	 * 全初期化する。
	 */
	public void reset() {
		audioTags.clear();
		videoTags.clear();
		audioEndFlg = false;
		videoEndFlg = false;
	}
	/**
	 * tagを追加する。
	 * @return
	 */
	public void addTag(Tag tag) throws Exception {
		// 対象のタグをaudioTagsとvideoTagsに登録する。
		if(tag instanceof AudioTag) {
			if(audioTags.size() == 0) {
				audioTags.add(tag);
			}
			else {
				Tag prevTag = audioTags.get(audioTags.size() - 1);
				if(prevTag.getTimestamp() > tag.getTimestamp()) {
					throw new Exception("同一メディアでデータのflipがありました。");
				}
				audioTags.add(tag);
			}
		}
		else if(tag instanceof VideoTag) {
			if(videoTags.size() == 0) {
				videoTags.add(tag);
			}
			else {
				Tag prevTag = videoTags.get(videoTags.size() - 1);
				if(prevTag.getTimestamp() > tag.getTimestamp()) {
					throw new Exception("同一メディアでデータのflipがありました。2");
				}
				videoTags.add(tag);
			}
		}
	}
	/**
	 * tagを一括して追加する
	 * @param tags
	 * @throws Exception
	 */
	public void addTags(List<Tag> tags) throws Exception {
		for(Tag tag : tags) {
			addTag(tag);
		}
	}
	/**
	 * 確定済みのタグを取り出す
	 * @return
	 */
	public List<Tag> getCompleteTags() {
		List<Tag> result = new ArrayList<Tag>();
		// データが定まったtagを応答します。
		Tag videoTag = null;
		Tag audioTag = null;
		while(true) {
			// audioTagsとvideoTagsからデータを取り出して、順番に応答していきます。
			if(videoTag == null) {
				if(videoTags.size() == 0) {
					break;
				}
				videoTag = videoTags.get(0);
			}
			if(audioTag == null) {
				if(audioTags.size() == 0) {
					break;
				}
				audioTag = audioTags.get(0);
			}
			if(videoTag.getTimestamp() <= audioTag.getTimestamp()) {
				// videoTagの方が有用
				result.add(videoTags.remove(0));
				videoTag = null;
			}
			else {
				result.add(audioTags.remove(0));
				audioTag = null;
			}
		}
		if(audioEndFlg && audioTags.size() == 0) {
			result.addAll(videoTags);
			videoTags.clear();
		}
		if(videoEndFlg && videoTags.size() == 0) {
			result.addAll(audioTags);
			audioTags.clear();
		}
		return result;
	}
	/**
	 * 映像データがもうないというフラグ設定
	 */
	public void setNomoreVideo() {
		videoEndFlg = true;
	}
	/**
	 * 音声データがもうないというフラグ設定
	 */
	public void setNomoreAudio() {
		audioEndFlg = true;
	}
	@Override
	public String toString() {
		return "manager:\n" + audioTags.toString() + "\n" + videoTags.toString();
	}
}
