package com.ttProject.xuggle.raw;

import javax.sound.sampled.AudioFormat;

import com.ttProject.media.raw.AudioData;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;

/**
 * AudioDataをIAudioSamplesに変換するコンバーター
 * @author taktod
 *
 */
public class AudioConverter {
	/**
	 * audioDataからaudioSamplesを作成して応答します。
	 * @param audioData
	 * @return
	 */
	public IAudioSamples makeSamples(AudioData audioData) {
		// フォーマットから情報を復元する。
		AudioFormat format = audioData.getFormat();
		int channels = format.getChannels();
		int bit = format.getSampleSizeInBits();
		int sampleNum = (int)(audioData.getBuffer().remaining() * 8 / bit / channels);
		Format xformat = null;
		switch(bit) {
		case 16:
			xformat = Format.FMT_S16P;
			break;
		case 8:
//			xformat = Format.FMT_U8;
//			break;
		case 32:
//			xformat = Format.FMT_S32;
//			break;
		default:
			throw new RuntimeException("知らないbit数のデータでした。" + bit);
		}
		IAudioSamples samples = IAudioSamples.make(sampleNum, channels, xformat);
		samples.getData().put(audioData.getBuffer().array(), 0, 0, audioData.getBuffer().remaining());
		samples.setComplete(true, sampleNum, (int)format.getSampleRate(), channels, xformat, 0); // 時刻はこちらで計算しなければいけないか？
		return samples;
	}
}
