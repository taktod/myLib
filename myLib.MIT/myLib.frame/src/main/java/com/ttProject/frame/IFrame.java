package com.ttProject.frame;

import java.nio.ByteBuffer;

import com.ttProject.unit.IUnit;

/**
 * メディアデータのフレームインターフェイス
 * @author taktod
 */
public interface IFrame extends IUnit {
	/**
	 * pcが１つのメディアデータと認識するのに過不足ない状態のbuffer参照
	 * おもにxuggleで変換するときに渡すmedia情報をまとめるのが仕事
	 * @return
	 */
	public ByteBuffer getPackBuffer();
	/**
	 * 各メディアが保持しているデータ長参照
	 * コンテナから割り出す参考値
	 * 規定は存在しない。
	 * (音声の場合はsampleNum / sampleRateでだせる。)
	 * (映像の場合は算出方法はないので、fpsから割り出す。)
	 */
	public float getDuration();
}
