package com.ttProject.container.flv;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.flv.type.VideoTag;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.h264.type.SliceIDR;

/**
 * frameデータからflvTagを生成して応答する変換動作
 * @author taktod
 */
public class FrameToFlvTagConverter {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FrameToFlvTagConverter.class);
	// msh用のデータ変更があったら応答しておきたいところ。
	private DecoderSpecificInfo  dsi = null;
	private SequenceParameterSet sps = null;
	private PictureParameterSet  pps = null;
	/**
	 * FlvTagリストを取得します。
	 * @return
	 */
	public List<FlvTag> getTags(IFrame frame) throws Exception {
		if(frame instanceof VideoFrame) {
			// 映像フレームの処理
			return getVideoTags((VideoFrame)frame);
		}
		else if(frame instanceof AudioFrame) {
			// 音声フレームの処理
			return getAudioTags((AudioFrame)frame);
		}
		throw new Exception("音声でも映像でもないフレームを検知しました。" + frame.toString());
	}
	/**
	 * 音声フレームについて処理します
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getAudioTags(AudioFrame frame) throws Exception {
		List<FlvTag> result = new ArrayList<FlvTag>();
		// aacの場合はmshチェックをしておく
		if(frame instanceof AacFrame) {
			Frame aacFrame = (Frame) frame;
			DecoderSpecificInfo dsi = aacFrame.getDecoderSpecificInfo();
			if(this.dsi == null || this.dsi.getData().compareTo(dsi.getData()) != 0) {
				this.dsi = dsi;
				AudioTag audioTag = new AudioTag();
				audioTag.setAacMediaSequenceHeader(aacFrame, dsi);
				result.add(audioTag);
			}
		}
		// audioTagをつくっておく
		AudioTag audioTag = new AudioTag();
		audioTag.addFrame(frame);
		result.add(audioTag);
		return result;
	}
	/**
	 * 映像フレームについて処理します
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getVideoTags(VideoFrame frame) throws Exception {
		List<FlvTag> result = new ArrayList<FlvTag>();
		// h264の場合はmshのチェックを実施する
		if(frame instanceof H264Frame) {
			if(frame instanceof SliceIDR) {
				SliceIDR sliceIDR = (SliceIDR)frame;
				if(sps == null || pps == null
				|| sps.getData().compareTo(sliceIDR.getSps().getData()) != 0
				|| pps.getData().compareTo(sliceIDR.getPps().getData()) != 0) {
					sps = sliceIDR.getSps();
					pps = sliceIDR.getPps();
					VideoTag videoTag = new VideoTag();
					videoTag.setH264MediaSequenceHeader(sliceIDR, sps, pps);
					result.add(videoTag);
				}
			}
			if(sps == null || pps == null) {
				// sps ppsが存在しない場合は処理しない。
				return result;
			}
		}
		// videoTagをつくっておく
		VideoTag videoTag = new VideoTag();
		videoTag.addFrame(frame);
		result.add(videoTag);
		return result;
	}
}
