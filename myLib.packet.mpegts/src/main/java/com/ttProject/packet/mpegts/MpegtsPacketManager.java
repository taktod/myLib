package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;

import com.ttProject.packet.IMediaPacket;
import com.ttProject.packet.MediaPacketManager;

/**
 * 基本的にデータを受け取ったらそのデータをメモリーにとっておいて、必要な秒数分の音声と映像データが入手できたらOKみたいな感じ。
 * 音声データは必要があれば、分解して再構築する必要あり。
 * このパケットデータが指定された秒数分のファイルデータとなります。
 * Sdt Pat Pmt [keyFrame Audio innerFrame] [keyFrame Audio innerFrame]
 * となるようにしておきたいと思います。
 * @author taktod
 *
 */
public class MpegtsPacketManager extends MediaPacketManager {
	/**
	 * パケットの内容を解析して、必要な時間分のデータ(Packetを応答します)
	 */
	@Override
	protected IMediaPacket analizePacket(ByteBuffer buffer) {
		// 処理中のパケットデータを参照
		// 処理中のパケットデータがなければ、新しいパケットデータを作成
		// 処理中のパケットデータにデータを追加
		// 指定秒数以上データがたまっていたら応答を実施する。
		// でOK
		// manager側でmpegtsのデータを管理して、送った方がいいと思われます。
		return null;
	}
	/**
	 * 拡張子応答
	 */
	@Override
	public String getExt() {
		return ".ts";
	}
	/**
	 * リストファイルの拡張子応答
	 */
	@Override
	public String getHeaderExt() {
		return ".m3u8";
	}
}
