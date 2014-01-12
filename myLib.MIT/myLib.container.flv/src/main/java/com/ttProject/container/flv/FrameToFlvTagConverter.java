package com.ttProject.container.flv;

import java.util.List;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.adpcmswf.AdpcmswfFrame;
import com.ttProject.frame.flv1.Flv1Frame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.frame.vp6.Vp6Frame;

/**
 * frameデータからflvTagを生成して応答する変換動作
 * @author taktod
 */
public class FrameToFlvTagConverter {
	// msh用のデータ変更があったら応答しておきたいところ。
	private DecoderSpecificInfo  dsi = null;
	private SequenceParameterSet sps = null;
	private PictureParameterSet  pps = null;
	/**
	 * FlvTagリストを取得します。
	 * @return
	 */
	public List<FlvTag> getFrames(IFrame frame) throws Exception {
		if(frame instanceof VideoFrame) {
			// 映像フレームの処理
			return getVideoFrames((VideoFrame)frame);
		}
		else if(frame instanceof AudioFrame) {
			// 音声フレームの処理
			return getAudioFrames((AudioFrame)frame);
		}
		throw new Exception("音声でも映像でもないフレームを検知しました。" + frame.toString());
	}
	/**
	 * 音声フレームについて処理します
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getAudioFrames(AudioFrame frame) {
		
		if(frame instanceof AacFrame) {
			
		}
		else if(frame instanceof Mp3Frame) {
			
		}
		else if(frame instanceof NellymoserFrame) {
			
		}
		else if(frame instanceof SpeexFrame) {
			
		}
		else if(frame instanceof AdpcmswfFrame) {
			
		}
		return null;
	}
	/**
	 * 映像フレームについて処理します
	 * @param frame
	 * @return
	 */
	private List<FlvTag> getVideoFrames(VideoFrame frame) {
		if(frame instanceof Flv1Frame) {
			
		}
		else if(frame instanceof Vp6Frame) {
			
		}
		else if(frame instanceof H264Frame) {
			
		}
		return null;
	}
}
