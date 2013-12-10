package com.ttProject.unit;

/**
 * すべてのメディアunitの基本となるクラス
 * こういうクラスの参照データは、abstractクラスに持たせておいたほうが見通しがよくなりそう。
 * 内容構成bitデータは、絶対に入るものは、宣言時にbitを構築しておく。
 * 必要があれば入るものは、nullをいれておく。
 * コンストラクタで入るものは、finalをつけておく。
 * @author taktod
 */
public abstract class Unit extends Data implements IUnit {
	/** pts値 */
	private long pts = 0L;
	/** timebase値*/
	private long timebase = 1000L;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPts() {
		return pts;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimebase() {
		return timebase;
	}
	protected void setPts(long pts) {
		this.pts = pts;
	}
	protected void setTimebase(long timebase) {
		this.timebase = timebase;
	}
}
