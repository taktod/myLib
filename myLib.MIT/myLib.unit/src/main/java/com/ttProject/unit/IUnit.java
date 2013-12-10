package com.ttProject.unit;

/**
 * 各unitの最少単位(flvのtagとかh264のframeとか)
 * @author taktod
 */
public interface IUnit extends IData {
	/**
	 * pts値(presentationTimestamp)
	 * @return
	 */
	public long getPts();
	/**
	 * unitのtimebase(時間の単位を記入しておきます。)
	 * flvの場合は1ミリ秒ベースなので1000
	 * mpegtsの場合は1/90000秒ベースなので90000といった感じ。(性格には、1/2400000?のもあるけど)
	 * @return
	 */
	public long getTimebase();
}
