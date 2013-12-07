package com.ttProject.frame;

public interface IAudioFrame extends IFrame {
	/**
	 * unitの持つサンプル数を応答します
	 * @return
	 */
	public int getSampleNum();
	/**
	 * unitのサンプルレートを応答します。
	 * @return
	 */
	public int getSampleRate();
	/**
	 * unitのチャンネル数を応答します。
	 * @return
	 */
	public int getChannel();
	/**
	 * サンプルの動作bitを応答します。
	 * @return
	 */
	public int getBit();
}
