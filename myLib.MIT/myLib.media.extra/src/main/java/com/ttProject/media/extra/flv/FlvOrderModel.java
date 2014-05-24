/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.extra.flv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.extra.mp4.Meta;
import com.ttProject.media.extra.mp4.Sond;
import com.ttProject.media.extra.mp4.Vdeo;
import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.FlvTagOrderManager;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.amf.Amf0Object;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mp4からflvのデータを取り出すモデル
 * flvTagの状態で取り出したい。
 * 一度つかったら2度とつかえませんので、2度目やる場合はnewしなおしてください。
 * 
 * keyFramesのデータに嘘をいれて、flowplayerで動作できるように調整しておきました。
 * @author taktod
 */
public class FlvOrderModel {
	private Logger logger = Logger.getLogger(FlvOrderModel.class);
	/** 終了イベントの取得動作用 */
	private IFlvStartEventListener startEventListener = null;
	/** 処理途上で捨てたデータのサイズ保持 */
	private int disposeDataSize = 0;
	/** 分割用 */
	private int divCount = 30;
	// 解析をすすめたい。
	private Vdeo vdeo = null;
	private Sond sond = null;
	private Meta meta = null;
	private int startMilliSeconds;
	private FlvTagOrderManager orderManager = new FlvTagOrderManager();
	private VideoTag videoMshTag = null;
	private AudioTag audioMshTag = null;
	/**
	 * コンストラクタ
	 */
	public FlvOrderModel(IReadChannel idxFile, boolean videoFlg, boolean soundFlg, int startMilliSecond) throws Exception {
		this.startMilliSeconds = startMilliSecond;
		initialize(idxFile, videoFlg, soundFlg);
	}
	/**
	 * 開始位置イベントリスナーを設定しておきます。
	 * @param startEventListener
	 */
	public void addStartEvent(IFlvStartEventListener startEventListener) {
		this.startEventListener = startEventListener;
	}
	/**
	 * flvのheaderを応答しておく
	 * なお、開始位置が遅すぎて対象パケットがない場合もありうるので、ほんとうは注意が必要
	 * @return
	 */
	public FlvHeader getFlvHeader() {
		FlvHeader flvHeader = new FlvHeader();
		flvHeader.setVideoFlg(vdeo != null);
		flvHeader.setAudioFlg(sond != null);
		return flvHeader;
	}
	/**
	 * 映像のmshデータを参照します
	 * @return
	 */
	public VideoTag getVideoMsh() {
		return videoMshTag;
	}
	/**
	 * 音声のmshデータを参照します
	 * @return
	 */
	public AudioTag getAudioMsh() {
		return audioMshTag;
	}
	/**
	 * 開始準備をしておく。
	 */
	private void initialize(IReadChannel tmp, boolean videoFlg, boolean soundFlg) throws Exception {
		while(tmp.position() < tmp.size()) {
			int position = tmp.position();
			ByteBuffer buffer = BufferUtil.safeRead(tmp, 8);
			int size = buffer.getInt();
			String tag = BufferUtil.getDwordText(buffer);
			if("vdeo".equals(tag)) {
				if(videoFlg) {
					vdeo = new Vdeo(position, size);
					vdeo.analyze(tmp);
				}
				tmp.position(position + size);
			}
			else if("sond".equals(tag)) {
				if(soundFlg) {
					sond = new Sond(position, size);
					sond.analyze(tmp);
				}
				tmp.position(position + size);
			}
			else if("meta".equals(tag)) {
				meta = new Meta(position, size);
				meta.analyze(tmp);
				tmp.position(position + size);
			}
		}
		if(vdeo == null && meta != null) {
			meta.setHeight(0);
			meta.setWidth(0);
		}
		if(sond != null) {
			sond.getStco().start(tmp, false); // dataPos
			sond.getStsc().start(tmp, false); // samples in chunk
			sond.getStsz().start(tmp, false); // sample size
			sond.getStts().start(tmp, false); // time
			sond.getStco().nextChunkPos(); // 次のchunkの位置を調べて置きます
			audioMshTag = sond.createFlvMshTag(tmp);
		}
		else {
			orderManager.setNomoreAudio();
		}
		if(vdeo != null) {
			vdeo.getStco().start(tmp, false); // dataPos
			vdeo.getStsc().start(tmp, false); // samples in chunk
			vdeo.getStsz().start(tmp, false); // sample size
			vdeo.getStss().start(tmp, false); // keyFrame
			vdeo.getStts().start(tmp, false); // time
			vdeo.getStco().nextChunkPos(); // 次のchunkの位置を調べて置きます。
			vdeo.getStss().nextKeyFrame(); // 初めのキーフレームの位置について調べておく
			videoMshTag = vdeo.createFlvMshTag(tmp);
		}
		else {
			orderManager.setNomoreVideo();
		}
	}
	// すでに応答を返しているかフラグ
	private boolean startResponse = false;
	/**
	 * もうデータがない場合はnullまだある場合はlistを返します。
	 * @return
	 */
	public List<Tag> nextTagList(IReadChannel source) throws Exception {
		// データを解析する。
			// 映像のstcoと音声のstcoを比較して前にある方の処理をすすめる。
		if(vdeo != null && (sond == null || vdeo.getStco().getChunkPos() < sond.getStco().getChunkPos())) {
			// 映像の方が前にあるので、映像の処理をすすめます。
			analyzeVdeo(source);
		}
		else if(sond != null) {
			// 音声の方が前にあるので、音声の処理をすすめます。
			analyzeSond(source);
		}
		else {
			// 両方nullだった場合はどうしようもないです。
			return null;
		}
		// 応答すべきflvTagを集める。
		List<Tag> result = orderManager.getCompleteTags();
		if(startResponse) { // すでにデータの応答中ならそのまま返す。
			return result;
		}
		while(result.size() > 0) {
			Tag tag = result.get(0); // 先頭をとってみる。
			MetaTag metaTag = null;
			if(vdeo == null) {
				// vdeoデータがない場合はaudioのみの動作になる。
				// 先頭にmshのデータを追加してあとは普通に応答すればOK
				if(audioMshTag != null) {
					audioMshTag.setTimestamp(tag.getTimestamp());
					result.add(0, audioMshTag);
				}
				// メタデータ用のmshをくっつけておく。
				if(meta != null) {
					metaTag = meta.createFlvMetaTag();
					metaTag.setTimestamp(tag.getTimestamp());
					// injectテスト
					Amf0Object<String, List<Double>> keyframes = new Amf0Object<String, List<Double>>();
					List<Double> times = new ArrayList<Double>();
					List<Double> filepositions = new ArrayList<Double>();
					int delta = (int)(meta.getDuration() / divCount);
					for(int i = 0;i < meta.getDuration();i += delta) {
						times.add(i * 0.001);
						filepositions.add(i * 1.0);
					}
					keyframes.put("times", times);
					keyframes.put("filepositions", filepositions);
					metaTag.putData("keyframes", keyframes);
					result.add(0, metaTag);
				}
				startResponse = true;
				if(startEventListener != null) {
					startEventListener.start(13 + metaTag.getSize() + getSize());
				}
				break;
			}
			else {
				// vdeoがある場合は動画のデータ
				// keyFrameが来るまでデータを捨てる必要がある。
				if(!(tag instanceof VideoTag) || !((VideoTag)tag).isKeyFrame()) {
					disposeDataSize += tag.getSize();
					result.remove(0); // 先頭のデータは必要ないので、捨てる
					continue;
				}
				// 第一キーフレームなので、ここからはじめることにする。
				if(audioMshTag != null) {
					audioMshTag.setTimestamp(tag.getTimestamp());
					result.add(0, audioMshTag);
				}
				if(videoMshTag != null) {
					videoMshTag.setTimestamp(tag.getTimestamp());
					result.add(0, videoMshTag);
				}
				if(meta != null) {
					metaTag = meta.createFlvMetaTag();
					metaTag.setTimestamp(tag.getTimestamp());
					// injectテスト
					Amf0Object<String, List<Double>> keyframes = new Amf0Object<String, List<Double>>();
					List<Double> times = new ArrayList<Double>();
					List<Double> filepositions = new ArrayList<Double>();
					// この部分がおおきすぎるとoverflowするらしい。こまったもんだ。
					int delta = (int)(meta.getDuration() / divCount);
					logger.info("delta:" + delta);
					for(int i = 0;i < meta.getDuration();i += delta) {
						times.add(i * 0.001);
						filepositions.add(i * 1.0);
					}
					keyframes.put("times", times);
					keyframes.put("filepositions", filepositions);
					metaTag.putData("keyframes", keyframes);
					result.add(0, metaTag);
				}
				startResponse = true;
				if(startEventListener != null) {
					startEventListener.start(13 + metaTag.getSize() + getSize());
				}
				break;
			}
		}
		// 応答
		return result;
	}
	private long vTimePos = 0;
	private long sTimePos = 0;
	private int vSampleCount = 0;
	private void analyzeVdeo(IReadChannel source) throws Exception {
		int sourcePos = vdeo.getStco().getChunkPos();
		vdeo.getStsc().nextChunk();
		int chunkSampleCount = vdeo.getStsc().getSampleCount();
		for(int i = 0;i < chunkSampleCount;i ++) {
			int sampleSize = vdeo.getStsz().nextSampleSize();
			if(sampleSize == -1) {
				throw new Exception("sampleSizeが取得できませんでした。");
			}
			vSampleCount ++;
			boolean isKeyFrame = (vdeo.getStss().getKeyFrame() == vSampleCount);
			if(isKeyFrame) {
				// キーフレーム
				vdeo.getStss().nextKeyFrame();
			}
			if(vTimePos * 1000 / vdeo.getTimescale() >= startMilliSeconds) {
				// 書き込むべきデータ
				VideoTag tag = new VideoTag();
				tag.setCodec(CodecType.AVC);
				tag.setFrameType(isKeyFrame);
				tag.setTimestamp((int)(vTimePos * 1000 / vdeo.getTimescale()));
				source.position(sourcePos);
				tag.setData(source, sampleSize);
				orderManager.addTag(tag);
			}
			else {
				disposeDataSize += 11 + 4 + 1 + 4 + sampleSize;
			}
			// 時間を更新しておく。
			int delta = vdeo.getStts().nextDuration();
			if(delta == -1) {
				// 最後まで読み込みが完了しているので、次のデータがとれない。
				logger.info("delta値を最後まで読み取ったのでおわりとします。");
				break;
			}
			sourcePos += sampleSize;
			vTimePos += delta;
		}
		// 一番最後まで処理して、データがもうない場合(stcoが切れた場合)vdeo = nullにしておく。
		if(!vdeo.getStco().hasMore()) {
			logger.info("stcoのデータがこれ以上ないみたいです。");
			orderManager.setNomoreVideo();
			vdeo = null;
		}
		else {
			vdeo.getStco().nextChunkPos();
		}
	}
	private void analyzeSond(IReadChannel source) throws Exception {
		int sourcePos = sond.getStco().getChunkPos();
		sond.getStsc().nextChunk();
		int chunkSampleCount = sond.getStsc().getSampleCount();
		for(int i = 0;i < chunkSampleCount;i ++) {
			int sampleSize = sond.getStsz().nextSampleSize();
			if(sampleSize == -1) {
				throw new Exception("sampleSizeが取得できませんでした。");
			}
			if(sTimePos * 1000 / sond.getTimescale() > startMilliSeconds) {
				// 書き込むべきデータ
				AudioTag tag = new AudioTag();
				if(sond.getMsh() == null) {
					tag.setCodec(CodecType.MP3);
				}
				else {
					tag.setCodec(CodecType.AAC);
				}
				tag.setChannels(sond.getChannelCount());
				tag.setSampleRate(sond.getSampleRate());
				tag.setTimestamp((int)(sTimePos * 1000 / sond.getTimescale()));
				source.position(sourcePos);
				tag.setData(source, sampleSize);
				orderManager.addTag(tag);
			}
			else {
				if(sond.getMsh() == null) {
					disposeDataSize += 11 + 4 + 1 + sampleSize;
				}
				else {
					disposeDataSize += 11 + 4 + 2 + sampleSize;
				}
			}
			int delta = sond.getStts().nextDuration();
			if(delta == -1) {
				break;
			}
			sourcePos += sampleSize;
			sTimePos += delta;
		}
		if(!sond.getStco().hasMore()) {
			orderManager.setNomoreAudio();
			sond = null;
		}
		else {
			sond.getStco().nextChunkPos();
		}
	}
	public int getSize() {
		int size = 0;
		if(vdeo != null) {
			size += vdeo.getTotalFlvSize();
		}
		if(sond != null) {
			size += sond.getTotalFlvSize();
		}
		return size - disposeDataSize;
	}
}
