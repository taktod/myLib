/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.transcode.xuggle.track;

import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * audio処理の細部抜き出し
 * @author taktod
 */
public class AudioTrackModule extends TrackModule {
	/** trackManager参照 */
	private final XuggleTrackManager trackManager;
	/** 処理パケット(可能なら使い回します) */
	private IPacket packet = null;
	/** リサンプル動作(必要なら実行します) */
	private IAudioResampler resampler = null;
	/**
	 * コンストラクタ
	 * @param trackManager
	 */
	protected AudioTrackModule(XuggleTrackManager trackManager) {
		this.trackManager = trackManager;
	}
	/**
	 * 処理の実行
	 * @param xuggleObject IAudioSamplesであることを期待しています。
	 */
	@Override
	protected void process(Object xuggleObject) {
		try {
			// データの再確認(ひっかかることはまずないはず。)
			if(!(xuggleObject instanceof IAudioSamples)) {
				throw new Exception("データがAudioSamplesではありません。異常です。");
			}
			IAudioSamples samples = (IAudioSamples) xuggleObject;
			// エンコーダー参照
			IStreamCoder encoder = trackManager.getEncoder();
			if(encoder == null) {
				throw new Exception("プロセス開始前にencoderが設定されていませんでした。");
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
//						audioResampler.delete();
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
				trackManager.applyData(packet);
			}
		}
		catch(Exception e) {
			// 例外が発生した場合は通知しておく。
			trackManager.reportException(e);
		}
	}
	/**
	 * オブジェクトがこのプログラムで動作可能か応答する
	 * @return true:可能 false:不能
	 */
	@Override
	protected boolean checkObject(Object xuggleObject) {
		if(!(xuggleObject instanceof IAudioSamples)) {
			// audio用の処理でなければスルー
			return false;
		}
		IAudioSamples samples = (IAudioSamples) xuggleObject;
		if(!samples.isComplete()) {
			// データが完成していなかったらスルー
			return false;
		}
		return true;
	}
	/**
	 * 終了処理
	 */
	@Override
	protected void close() {
	}
}
