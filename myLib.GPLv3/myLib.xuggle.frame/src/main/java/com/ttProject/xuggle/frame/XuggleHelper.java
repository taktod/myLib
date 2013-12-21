package com.ttProject.xuggle.frame;

import java.util.List;

import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
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
	public static List<IPacket> getPackets(IFrame frame) throws Exception {
		// packetからbyteBufferを取り出してあとは適当に処理する。
		if(frame instanceof AudioMultiFrame || frame instanceof VideoMultiFrame) {
			throw new Exception("マルチフレームからは情報がとれないようになっています");
		}
		if(frame instanceof IAudioFrame) {
			// 音声frame
		}
		else if(frame instanceof IVideoFrame) {
			// 映像frame
			// bufferの部分を抜き出す必要あり。
			System.out.println(HexUtil.toHex(frame.getPackBuffer(), 0, 20, true));
			System.out.println(frame.getPts());
			// TODO ここではkeyFrameかという情報とtimestampもほしいところ。
			// よってこれらの情報をいれないと処理できない。
			// keyFrameについては、それぞれのframeによる
			// timestampは別途設定してもらわないとどうしようもない。
		}
		// その他
		return null;
	}
	/**
	 * 各packetを取り出します。
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private static IPacket getPacket(IFrame frame) throws Exception {
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
