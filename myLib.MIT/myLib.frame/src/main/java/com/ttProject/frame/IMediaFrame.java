package com.ttProject.frame;

import com.ttProject.unit.IUnit;

/**
 * メディアデータのフレームインターフェイス
 * @author taktod
 */
public interface IMediaFrame extends IUnit {
	public long getPts();
	public long getTimebase();
}
