package com.ttProject.transcode.xuggle.encode;

import java.util.List;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITranscodeListener;
import com.ttProject.transcode.xuggle.packet.IDepacketizer;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * audioエンコード動作
 * @author taktod
 * audioGapがある場合は補完しないとだめなんだろうか・・・そこだけ心配
 */
public class AudioEncodeManager implements IEncodeManager {
	private IPacket packet = null;
	private IAudioResampler resampler = null;
	private boolean threadFlg = false;
	private IStreamCoder encoder = null;
	private IDepacketizer depacketizer = null;
	private ITranscodeListener listener = null;
	/**
	 * 変換結果出力先を保持しておく。
	 * @param listener
	 */
	public void setTranscodeListener(ITranscodeListener listener) {
		this.listener = listener;
	}
	/**
	 * 閉じる動作
	 */
	public void close() {
		if(resampler != null) {
			resampler = null;
		}
		if(encoder != null) {
			encoder.close();
			encoder = null;
		}
		if(depacketizer != null) {
			depacketizer = null;
		}
		if(listener != null) {
			listener = null;
		}
	}
	/**
	 * エンコーダーを設定します
	 * @param encoder
	 * @throws Exception
	 */
	public void setEncoder(IStreamCoder encoder) throws Exception {
		if(encoder.getDirection() == Direction.DECODING) {
			throw new Exception("デコーダーが設定されています");
		}
		if(encoder.getCodecType() != Type.CODEC_TYPE_AUDIO) {
			throw new Exception("音声エンコーダーではありません。");
		}
		// 設定しておく。
		this.encoder = encoder;
	}
	/**
	 * パケットを分解する動作オブジェクトを設定する。
	 * @param depacketizer
	 */
	public void setDepacketizer(IDepacketizer depacketizer) {
		this.depacketizer = depacketizer;
	}
	/**
	 * エンコード処理実体
	 * @param samples
	 * @throws Exception
	 */
	public void encode(Object xuggleObject) throws Exception {
		if(!(xuggleObject instanceof IAudioSamples)) {
			return;
		}
		IAudioSamples samples = (IAudioSamples) xuggleObject;
		if(!samples.isComplete()) {
			// completeしていなかったら処理できません。
			return;
		}
		// thread動作かどうか
		process(samples);
	}
	/**
	 * エンコード内部処理
	 * @param samples
	 * @throws Exception
	 */
	private void process(IAudioSamples samples) throws Exception {
		if(encoder == null) {
			return;
		}
		// データのフォーマットが一致しているか確認します。
		if(samples.getSampleRate() != encoder.getSampleRate()
		|| samples.getFormat() != encoder.getSampleFormat()
		|| samples.getChannels() != encoder.getChannels()) {
			// 一致しない場合はリサンプルする必要あり
			// resamplerをつくってリサンプルする必要あり。
			if(resampler == null
			|| resampler.getOutputRate() != encoder.getSampleRate()
			|| resampler.getOutputFormat() != encoder.getSampleFormat()
			|| resampler.getOutputChannels() != encoder.getChannels()) {
				if(resampler != null) {
					// これ消さなくてもいいかも・・・xuggleがエラーになる場合はコメントアウトしておきたい。
//					audioResampler.delete();
				}
				resampler = IAudioResampler.make(
						encoder.getChannels(), samples.getChannels(),
						encoder.getSampleRate(), samples.getSampleRate(),
						encoder.getSampleFormat(), samples.getFormat());
			}
			IAudioSamples spls = IAudioSamples.make(1024, encoder.getChannels());
			int retval = resampler.resample(spls, samples, samples.getNumSamples());
			if(retval <= 0) {
				throw new Exception("音声のリサンプルに失敗しました。");
			}
			samples = spls;
		}
		// エンコード実行
		int samplesConsumed = 0;
		while(samplesConsumed < samples.getNumSamples()) {
			if(packet == null) {
				packet = IPacket.make();
			}
			if(!encoder.isOpen()) {
				if(encoder.open(null, null) < 0) {
					throw new Exception("エンコーダーが開けませんでした");
				}
			}
			int retval = encoder.encodeAudio(packet, samples, samplesConsumed);
			if(retval < 0) {
				throw new Exception("音声変換失敗");
			}
			samplesConsumed += retval;
			if(packet.isComplete()) {
				// できあがったので登録しておく。
				if(depacketizer != null) {
					List<Unit> units = depacketizer.getUnits(encoder, packet);
					// listenerに渡す必要あり。
					if(listener != null) {
						listener.receiveData(units);
					}
				}
			}
		}
	}
}
