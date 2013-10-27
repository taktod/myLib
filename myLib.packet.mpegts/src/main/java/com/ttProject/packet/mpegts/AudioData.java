package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.IAudioData;
import com.ttProject.media.aac.AacManager;
import com.ttProject.media.aac.Frame;
import com.ttProject.media.aac.FrameAnalyzer;
import com.ttProject.media.aac.IFrameAnalyzer;
import com.ttProject.media.aac.frame.Aac;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * 音声データ保持オブジェクト
 * 音声データは、映像データと完全に同期させるのが難しいので、いったんpesからaacもしくはmp3のframeに分解後再構築させることにします。
 * @author taktod
 */
public class AudioData extends MediaData {
	/** ロガー */
	private final Logger logger = Logger.getLogger(AudioData.class);
	/** 分解前のpesデータリスト(1パケット分だけ保持する形) */
	private final List<Pes> pesList = new ArrayList<Pes>();
	/** 分解後のIAudioDataリスト */
	private final List<IAudioData> audioDataList = new ArrayList<IAudioData>();
	private long counter = 0; // 開始位置からの保持データ量
	private long startPos = 0; // データ開始位置のpts値
	@Override
	public void analyzePes(Pes pes) {
		if(!checkPes(pes)) {
			return;
		}
		// 自分用のデータなので対処しておく。
		// 音声データのpesは分解して、aacもしくはmp3に変更してしまっておく。
		// adaptationFieldのrandomAccessIndicator
		if(pes.isPayloadUnitStart()) {
			ByteBuffer buffer = ByteBuffer.allocate(188 * pesList.size());
			while(pesList.size() > 0) {
				Pes p = pesList.remove(0);
				buffer.put(p.getRawData());
			}
			buffer.flip(); // aacもしくはmp3として分解できそうなデータはそろった。あとは分解して、配列に突っ込んでおくだけ。
			IReadChannel bufferChannel = new ByteReadChannel(buffer);
			try {
				IFrameAnalyzer analyzer = new FrameAnalyzer();
				Frame frame = null;
				while((frame = analyzer.analyze(bufferChannel)) != null) {
					logger.info(frame);
					if(frame instanceof Aac) {
						audioDataList.add((Aac) frame);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			logger.info(buffer.remaining());
			// たまっている前のデータについて調整しておく。
			// byteBufferのchunkに変更し、
			// aacManagerで解析して、aac化
			// その後aacのデータ群として保持しておけばOK
		}
		pesList.add(pes);
	}
}
