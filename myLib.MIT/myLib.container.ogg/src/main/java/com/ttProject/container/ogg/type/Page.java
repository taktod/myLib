package com.ttProject.container.ogg.type;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.ogg.OggPage;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * oggの基本単位のpage
 * @see http://www.xiph.org/vorbis/doc/framing.html
 * @see http://ja.wikipedia.org/wiki/Ogg%E3%83%9A%E3%83%BC%E3%82%B8
 * 内容は次のような感じ
 * pageの開始
 * 4バイト:OggS
 * 1バイト:stream_structure_version (現在は0x00のみ)
 * 1バイト:bitFlag 0000 0abc c:フラグが立っていたら続きpacket b:フラグがたっていたらロジックストリームの開始のページ a:フラグがたっていたらロジックストリームの最後のページ
 * 
 * 8バイト:absoluteGranulePosition 位置情報(含有物次第の値らしい)
 * 4バイト:streamSerialNumber とりあえずなにがしの番号
 * 4バイト:pageSequenceNo ページの番号 mpegtsのcounterみたいなもんかな
 * 4バイト:pageChecksum headerから導くCRC値らしい
 * 1バイト:ページが保持するsegmentsの数
 * 以下セグメントデータ
 *  1バイト:セグメントサイズ(Nとする) ←segmentsの数だけならぶ
 *  Nバイト:セグメント実体 ←segmentsの数だけならぶ
 * みたいな感じになってる。
 * 
 * 以下これの繰り返しっぽい。
 * avconvでつくったoggデータの確認しつつやってみた結果。
 * どこか正しくないことがあっても怒らないこと。
 * 
 * @author taktod
 */
public class Page extends OggPage {
	/** ロガー */
	private Logger logger = Logger.getLogger(Page.class);
	/**
	 * コンストラクタ
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public Page(Bit8 version, Bit5 zeroFill,
			Bit1 logicEndFlag, Bit1 logicStartFlag,
			Bit1 packetContinurousFlag) {
		super(version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		logger.info("minimumload");
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getPosition() + 27 + getSegmentSizeList().size());
		List<IFrame> frameList = getFrameList();
		for(Bit8 size : getSegmentSizeList()) {
			ByteBuffer buffer = BufferUtil.safeRead(channel, size.get());
			// 解析したい。
			IReadChannel bufferChannel = new ByteReadChannel(buffer);
			IFrame frame = (IFrame)getStartPage().getAnalyzer().analyze(bufferChannel);
			if(frame instanceof AudioFrame) {
				AudioFrame audioFrame = (AudioFrame) frame;
				audioFrame.setTimebase(audioFrame.getSampleRate());
				audioFrame.setPts(getStartPage().getPassedTic());
				getStartPage().setPassedTic(audioFrame.getPts() + audioFrame.getSampleNum());
			}
			frameList.add(frame);
		}
		// analyzerをつかって開く
		channel.position(getPosition() + getSize());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
