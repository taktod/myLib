package com.ttProject.media.mpegts;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ttProject.media.Manager;
import com.ttProject.media.mpegts.packet.Pat;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * データとしては、PATを読み込む→PMTのIDがわかる
 * PMTを読み込む→その他のメディアのIDがわかる
 * PCRのIDがわかる→時間情報がとれるようになる。
 * H264のIDがわかる→KeyFrameの位置がわかるようになる。
 * こんな感じ。
 * PCRのID、mediaID、h264IDは一致することもあるのでどれだということはわからないと思う。
 * @author taktod
 *
 */
public class MpegtsManager extends Manager<Packet> {
	/** PatID */
	private final int patId = 0x0000;
	/** PmtID */
	private Set<Integer> pmtIdSet = new HashSet<Integer>();
	private Integer pcrId = null;
	private Set<Integer> mediaIdSet = new HashSet<Integer>();
	private Set<Integer> h264IdSet = new HashSet<Integer>();
	@Override
	public List<Packet> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
	@Override
	public Packet getUnit(IReadChannel source) throws Exception {
		// 188バイトデータがあるか確認する。
		if(source.size() - source.position() < 188) {
			// 188バイト未満なのでmpegtsのpacketとして成立しない。
			return null;
		}
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 3);
		// 先頭が0x47になっているか確認する。
		if(buffer.get() != 0x47) {
			throw new Exception("先頭が0x47になっていませんでした。mpegtsとして成立していません。");
		}
		int pid = getPid(buffer); // 2バイト読み込んである
		// pidによってなんのデータであるか分岐させておく必要がある。
		if(pid == patId) {
			// pidがpatの場合はpatオブジェクトをつくる。
			// 内容も解析しておいてpmtについて調査しておく。
			Pat pat = new Pat(position);
			pat.analyze(source);
			return pat;
		}
		// 中身の実装
		return null;
	}
	private int getPid(ByteBuffer buffer) {
		return buffer.getShort() & 0x1FFF;
	}
}
