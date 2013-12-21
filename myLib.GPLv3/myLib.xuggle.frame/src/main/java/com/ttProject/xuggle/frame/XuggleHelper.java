package com.ttProject.xuggle.frame;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.util.HexUtil;
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
	public static IPacket getPacket(IFrame frame) throws Exception {
		// packetからbyteBufferを取り出してあとは適当に処理する。
		if(frame instanceof IAudioFrame) {
			// 音声frame
		}
		else if(frame instanceof IVideoFrame) {
			// 映像frame
			// bufferの部分を抜き出す必要あり。
			System.out.println(HexUtil.toHex(frame.getPackBuffer(), 0, 20, true));
			// TODO ここではkeyFrameかという情報とtimestampもほしいところ。
			// よってこれらの情報をいれないと処理できない。
			// keyFrameについては、それぞれのframeによる
			// timestampは別途設定してもらわないとどうしようもない。
		}
		// その他
		return null;
	}
	/**
	 * frameから対象デコーダーを取得する動作
	 * @param frame
	 * @return
	 */
	public static IStreamCoder getDecoder(IFrame frame) {
		return null;
	}
	/**
	 * packetからフレームをつくる動作
	 * @param packet
	 * @return
	 */
	public static IFrame getFrame(IPacket packet) {
		return null;
	}
}
