package com.ttProject.media;

/**
 * 音声の生データに追加することで、必要なデータを参照できるようにします。
 * 具体的にはmp3やaacといったframeになります。
 * @author taktod
 * frameNum / samplingRateを実行することで音声データの正確な時間データが取得できるようになります。
 * 
 * 例：1024サンプルがあるaacで、44100Hzのサンプルレートの場合は、1024 / 44100 = 0.02321秒のフレームとなります。
 */
public interface IAudioData extends IMediaData {
	/**
	 * 各データが持つframe数を応答します。
	 * @return
	 */
	public int getSampleNum();
	/**
	 * サンプリングレートを応答します。
	 */
	public int getSampleRate();
	/**
	 * データサイズを応答します。
	 * @return
	 */
	public int getSize();
}
