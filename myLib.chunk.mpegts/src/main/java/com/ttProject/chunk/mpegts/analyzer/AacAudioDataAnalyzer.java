package com.ttProject.chunk.mpegts.analyzer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.IAudioData;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * pesからaacのIAudioDataを取り出すAnalyzer
 * @author taktod
 */
public class AacAudioDataAnalyzer implements IAudioDataAnalyzer {
	/** ロガー */
	private Logger logger = Logger.getLogger(AacAudioDataAnalyzer.class);
	/** pesをいったんリストにいれてからpayLoadデータの取得がおわったらaacFrameに分解しなければいけない */
	private List<Pes> pesList = new ArrayList<Pes>();
	/** 処理中のpayloadのpts値 */
	private long lastPtsValue = 0L;
	/**
	 * 処理中のpayloadのpts値参照
	 * @return
	 */
	@Override
	public long getLastPtsValue() {
		return lastPtsValue;
	}
	/**
	 * pesからaudioDataを取り出す。
	 */
	@Override
	public List<IAudioData> analyzeAudioData(Pes pes) {
		List<IAudioData> result = null;
		if(pes.isPayloadUnitStart()) {
			// 次のpayLoadの開始時にいままでのデータを取得します。
			result = getRemainData();
		}
		pesList.add(pes);
		return result;
	}
	/**
	 * 残っているデータを取得します。
	 */
	@Override
	public List<IAudioData> getRemainData() {
		List<IAudioData> result = null;
		// payloadの開始位置の場合
		if(pesList.size() != 0) {
			// 前のデータがある場合
			ByteBuffer buffer = ByteBuffer.allocate(188 * pesList.size());
			while(pesList.size() > 0) {
				Pes p = pesList.remove(0);
				if(p.isPayloadUnitStart() && p.hasPts()) {
					lastPtsValue = p.getPts().getPts();
				}
				buffer.put(p.getRawData());
			}
			// 必要なaacデータがたまった。
			buffer.flip();
			IReadChannel bufferChannel = new ByteReadChannel(buffer);
			try {
				IFrameAnalyzer analyzer = new FrameAnalyzer();
				Frame frame = null;
				result = new ArrayList<IAudioData>();
				while((frame = analyzer.analyze(bufferChannel)) != null) {
					if(frame instanceof Aac) {
						result.add((Aac)frame);
					}
				}
			}
			catch(Exception e) {
				logger.error("例外が発生しました", e);
			}
		}
		return result;
	}
}
