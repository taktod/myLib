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
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;

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
	private List<FlvTag> getAudioFrames(AudioFrame frame) throws Exception {
		Bit4 codecId = new Bit4();
		Bit2 sampleRate = new Bit2();
		Bit1 bitCount = new Bit1();
		Bit1 channels = new Bit1();
		// codecIdと拡張データについて調整しておく必要あり。
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
		else {
			throw new Exception("未対応なaudioFrameでした:" + frame);
		}
		switch(frame.getChannel()) {
		case 1:
			channels.set(0);
			break;
		case 2:
			channels.set(1);
			break;
		default:
			throw new Exception("音声チャンネル数がflvに適合しないものでした。");
		}
		switch(frame.getBit()) {
		case 8:
			bitCount.set(0);
			break;
		case 16:
			bitCount.set(1);
			break;
		default:
			throw new Exception("ビット深度が適合しないものでした。");
		}
		switch((int)(frame.getSampleRate() / 100)) {
		case 55:
			sampleRate.set(0);
			break;
		case 110:
			sampleRate.set(1);
			break;
		case 220:
			sampleRate.set(2);
			break;
		case 441:
			sampleRate.set(3);
			break;
		default:
			throw new Exception("frameRateが適合しないものでした。");
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
