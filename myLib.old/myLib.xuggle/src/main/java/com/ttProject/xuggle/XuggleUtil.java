/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IVideoPicture;

/**
 * xuggleのオブジェクト用の動作補助
 * @author taktod
 */
public class XuggleUtil {
	/**
	 * audioSamplesのコピーを作成する
	 * @param samples
	 * @return
	 */
	public static IAudioSamples clone(IAudioSamples samples) {
		IAudioSamples result = IAudioSamples.make(samples.getNumSamples(), samples.getChannels(), samples.getFormat());
		result.getData().put(samples.getData().getByteArray(0, samples.getSize()), 0, 0, samples.getSize());
		result.setTimeBase(samples.getTimeBase());
		result.setTimeStamp(samples.getTimeStamp());
		result.setComplete(samples.isComplete(), samples.getNumSamples(), samples.getSampleRate(), samples.getChannels(), samples.getFormat(), samples.getPts());
		return result;
	}
	/**
	 * videoPicture
	 * @param picture
	 * @return
	 */
	public static IVideoPicture clone(IVideoPicture picture) {
		// TODO これで正しいか不明
		IVideoPicture result = IVideoPicture.make(picture);
		return result;
	}
}
