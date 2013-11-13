package com.ttProject.chunk.mpegts.analyzer;

import com.ttProject.chunk.mpegts.AudioDataList;
import com.ttProject.chunk.mpegts.VideoDataList;
import com.ttProject.media.Unit;
import com.ttProject.media.mpegts.packet.Pmt;

/**
 * h264のframeを解析してPesを作成します。
 * IAudioDataはスルー
 * @author taktod
 *
 */
public class H264PesAnalyzer implements IPesAnalyzer {
	@Override
	public void analyze(Unit unit) {
		if(unit instanceof Pmt) {
			
		}
	}
	@Override
	public void setAudioDataList(AudioDataList audioDataList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setVideoDataList(VideoDataList videoDataList) {
		// TODO Auto-generated method stub
		
	}
}
