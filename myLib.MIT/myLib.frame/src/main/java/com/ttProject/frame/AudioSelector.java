package com.ttProject.frame;
import com.ttProject.unit.ISelector;

/**
 * audio系のデータのselector動作
 * @author taktod
 * コンテナから渡された情報も保持しておいて、初期化時に、そのデータを渡しておく必要あり
 */
public abstract class AudioSelector implements ISelector {
	// 以下コンテナから読み取れるデフォルト、フレームのデータを構築するときに読み込むことにします。
	/** チャンネル数 */
	private int channel;
	/** ビット数 */
	private int bit;
	/** サンプルレート数 */
	private int sampleRate;
	/** サンプル数 */
	private int sampleNum;
	/**
	 * デフォルト値をなるべくいれておく
	 * @param frame
	 * @return
	 */
	public void setup(AudioFrame frame) {
		frame.setChannel(channel);
		frame.setBit(bit);
		frame.setSampleRate(sampleRate);
		frame.setSampleNum(sampleNum);
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public void setBit(int bit) {
		this.bit = bit;
	}
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	public void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}
}
