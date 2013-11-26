package com.ttProject.transcode.xuggle;

import java.util.concurrent.ExecutorService;

import com.ttProject.transcode.ITranscodeManager;
import com.ttProject.transcode.xuggle.packet.IPacketizer;

/**
 * xuggle用の特殊処理入りのinterface
 * @author taktod
 */
public interface IXuggleTranscodeManager extends ITranscodeManager {
	/**
	 * パケットの解析プログラムを設定します
	 * @param packetizer
	 */
	public void setPacketizer(IPacketizer packetizer);
	/**
	 * 動作時に利用できるexecutorServiceを登録します。
	 * note マルチスレッドで動作させることで動作を向上させることができます。
	 */
	public void setExecutorService(ExecutorService executor);
}
