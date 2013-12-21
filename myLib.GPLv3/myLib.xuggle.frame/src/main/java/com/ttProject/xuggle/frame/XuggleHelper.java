package com.ttProject.xuggle.frame;

import com.ttProject.frame.IFrame;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * flameデータをxuggleのpacketに変換するプログラム
 * @author taktod
 */
public class XuggleHelper {
	/**
	 * frameからpacketをつくる動作
	 * @param frame
	 * @return
	 */
	public IPacket getPacket(IFrame frame) {
		return null;
	}
	/**
	 * frameから対象デコーダーを取得する動作
	 * @param frame
	 * @return
	 */
	public IStreamCoder getDecoder(IFrame frame) {
		return null;
	}
	/**
	 * packetからフレームをつくる動作
	 * @param packet
	 * @return
	 */
	public IFrame getFrame(IPacket packet) {
		return null;
	}
}
