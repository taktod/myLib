package com.ttProject.media.flv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;

/**
 * Flvのtagのソートをおこなう
 * getTagsにデータをいれると、見合ったデータが返ってくる
 * audioとvideoのデータがflipしていてもいいが、audioだけ or videoだけでflipしていると扱えないものとする。
 * @author taktod
 */
public class FlvTagOrderManager {
	/** ロガー */
	private static final Logger logger = Logger.getLogger(FlvTagOrderManager.class);
	/** 処理待ちaudioTagリスト */
	private final List<Tag> audioTags = new ArrayList<Tag>();
	/** 処理待ちvideoTagリスト */
	private final List<Tag> videoTags = new ArrayList<Tag>();
	/** 各メディアトラックがもうなくなったかのフラグ */
	private boolean videoEndFlg = false;
	private boolean audioEndFlg = false;
	/** ソート用の比較オブジェクト */
	private final TagComparator tagSort = new TagComparator();
	/** 比較に用いるindex値を設定、設定数のタグは無条件で保持になります */
	private final int videoCompIndex = 5;
	// TODO ここでのaudioの完了までの判定indexが大きすぎて、音がないデータになった場合にこまったことになる。
	private final int audioCompIndex = 20; // ちょっと大きいけど、まぁconvertの遅延もあるし、いいだろw
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
	public synchronized void addTag(Tag tag) throws Exception {
		// 対象のタグをaudioTagsとvideoTagsに登録する。
		if(tag instanceof AudioTag) {
			if(audioTags.size() == 0) {
				audioTags.add(tag);
			}
			else {
				Tag prevTag = audioTags.get(audioTags.size() - 1);
				if(prevTag.getTimestamp() > tag.getTimestamp()) {
					logger.warn("音声データのflipがありました。prev:" + prevTag.getTimestamp() + ", current:" + tag.getTimestamp());
					if(audioTags.size() > audioCompIndex) {
						prevTag = audioTags.get(audioTags.size() - audioCompIndex);
						if(prevTag.getTimestamp() > tag.getTimestamp()) {
							logger.warn("再チャレンジ音声データのflipがありました。prev:" + prevTag.getTimestamp() + ", current:" + tag.getTimestamp());
							return;
						}
					}
				}
				audioTags.add(tag);
				Collections.sort(audioTags, tagSort);
			}
		}
		else if(tag instanceof VideoTag) {
			if(videoTags.size() == 0) {
				videoTags.add(tag);
			}
			else {
				Tag prevTag = videoTags.get(videoTags.size() - 1);
				if(prevTag.getTimestamp() > tag.getTimestamp()) {
					logger.warn("映像データのflipがありました。");
					if(videoTags.size() > videoCompIndex) {
						prevTag = videoTags.get(videoTags.size() - videoCompIndex);
						if(prevTag.getTimestamp() > tag.getTimestamp()) {
							logger.warn("再チャレンジ映像データのflipがありました。");
							return;
						}
					}
				}
				videoTags.add(tag);
				Collections.sort(videoTags, tagSort);
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
	public synchronized List<Tag> getCompleteTags() {
		List<Tag> result = new ArrayList<Tag>();
		// データが定まったtagを応答します。
		Tag videoTag = null;
		Tag audioTag = null;
		while(true) {
			// audioTagsとvideoTagsからデータを取り出して、順番に応答していきます。
			if(videoTag == null) {
				if(videoTags.size() < videoCompIndex) {
					break;
				}
				videoTag = videoTags.get(0);
			}
			if(audioTag == null) {
				if(audioTags.size() < audioCompIndex) {
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
		setVideoEndFlg(true);
	}
	public void setVideoEndFlg(boolean flg) {
		videoEndFlg = flg;
	}
	/**
	 * 音声データがもうないというフラグ設定
	 */
	public void setNomoreAudio() {
		setAudioEndFlg(true);
	}
	public void setAudioEndFlg(boolean flg) {
		audioEndFlg = flg;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "manager:\n" + audioTags.toString() + "\n" + videoTags.toString();
	}
	private class TagComparator implements Comparator<Tag> {
		@Override
		public int compare(Tag t1, Tag t2) {
			return t1.getTimestamp() - t2.getTimestamp();
		}
	}
}
