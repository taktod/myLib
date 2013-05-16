package com.ttProject.packet.mpegts;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import com.ttProject.packet.IMediaPacket;
import com.ttProject.packet.MediaPacketManager;

/**
 * httpLiveStreaming用のパケットを管理するマネージャー
 * TODO 今回は複数のファイルコンバートを同時に実行して、帯域によって、m3u8を切り替えることが可能にしたい。
 * なので、切るタイミングを他のm3u8と合わせる必要がでてくるかも？
 * @author taktod
 */
public class MpegtsPacketManager extends MediaPacketManager {
	/** PATIdデータ(全mpegts共通なのでstatic保持) */
	public static final int PATId = 0x0000;
	/** PMTIdデータ */
	private Set<Integer> pmtIdSet = new HashSet<Integer>();
	/** PCRIdデータ(Program Clock Reference) */
	private Integer PCRId = null;
	/** MediaIdデータ */
	private Set<Integer> mediaIdSet = new HashSet<Integer>();
	/** h.264Id */
	private Set<Integer> h264IdSet = new HashSet<Integer>();
	/** 開始位置 */
	private Long startPos = null;
	/** 現在位置 */
	private long currentPos;
	/**
	 * 拡張子応答
	 */
	@Override
	public String getExt() {
		return ".ts";
	}
	@Override
	public String getHeaderExt() {
		return ".m3u8";
	}
	/**
	 * pmtをpatから抽出済であるか確認する。
	 * @return
	 */
	private boolean isPmtChecked() {
		return pmtIdSet.size() > 0;
	}
	/**
	 * 入力pidがpmtのIdであるか確認する。
	 * @param pid
	 * @return
	 */
	public boolean isPmtId(int pid) {
		return pmtIdSet.contains(pid);
	}
	/**
	 * pmtIdにデータ追加
	 * @param pid
	 */
	public void addPmtId(int pid) {
		pmtIdSet.add(pid);
	}
	/**
	 * Pcr(program clock reference)のデータを追記する。
	 * @param pid
	 */
	public void setPcrId(int pid) {
		PCRId = pid;
	}
	/**
	 * PCRIdであるか判定する。
	 * @param pid
	 * @return true:一致する false:一致しない null:PCRIdが不明
	 */
	public Boolean isPcrId(int pid) {
		if(PCRId == null) {
			return null;
		}
		return pid == PCRId;
	}
	/**
	 * h264Idにデータを追加する。
	 */
	public void addH264Id(int pid) {
		h264IdSet.add(pid);
	}
	/**
	 * h264のidであるか確認
	 * @param pid
	 * @return
	 */
	public boolean isH264Id(int pid) {
		return h264IdSet.contains(pid);
	}
	/**
	 * mediaIdにデータを追加する。
	 * @param pid
	 */
	public void addMediaId(int pid) {
		mediaIdSet.add(pid);
	}
	/**
	 * mediaのidであるか確認
	 * @param pid
	 * @return
	 */
	public boolean isMediaId(int pid) {
		return mediaIdSet.contains(pid);
	}
	/**
	 * pcrの時刻情報の値を保持しておく
	 * (ただし24時間たつと前のデータに戻るのでそのあたり調整しておく必要あり。)
	 * @param tic
	 */
	public void setTimeTic(long tic) {
		if(startPos == null) {
			startPos = tic;
		}
		currentPos = tic;
	}
	/**
	 * 経過ticを応答する
	 * @return
	 */
	public long getPassedTic() {
		if(startPos == null) {
			return 0;
		}
		return currentPos - startPos;
	}
	/**
	 * パケットの内容を解析します。
	 * @param buffer
	 * @return
	 */
	@Override
	protected IMediaPacket analizePacket(ByteBuffer buffer) {
		IMediaPacket packet = getCurrentPacket();
		if(packet == null) {
			// mediaかheaderか決めなければいけない。
			if(isPmtChecked()) {
				// PMTIdがきまっているのでMedia決定
				packet = new MpegtsMediaPacket(this);
			}
			else {
				// PMTIdがきまっていないのでheader決定
				packet = new MpegtsHeaderPacket(this);
			}
		}
		if(packet.analize(buffer)) {
			setCurrentPacket(null);
			return packet;
		}
		else {
			setCurrentPacket(packet);
			return null;
		}
	}

}
