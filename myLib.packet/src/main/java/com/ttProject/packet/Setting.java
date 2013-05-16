package com.ttProject.packet;

public class Setting {
	private static Setting instance = new Setting();
	public static synchronized Setting getInstance() {
		return instance;
	}
	private Setting() {
		
	}
	/**
	 * packetに保持させるデータの長さを応答する
	 * とりあえずデバッグで2秒いれておく。
	 * @return
	 */
	public float getDuration() {
		return 2;
	}
}
