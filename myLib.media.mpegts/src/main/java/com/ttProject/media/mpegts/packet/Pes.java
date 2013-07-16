package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * ElementaryStreamPacket
 * (packetizedElementaryStream Packet)
 * 
 * 47 [abcd dddd] [dddd dddd] [eefg hhhh]
 * a:error indicator
 * b:payload unit start indicator
 * c:transport priority indicator
 * e:scranblingControl
 * f:has adaptation field
 * g:has payload data
 * h:continuty count
 * 
 * adaptationFieldをもっている場合
 * 次の1バイトがadaptation fieldの長さっぽい。
 * flagsとかがはいっているけど、とりあえずいらないので、捨てればよさそう。
 * 
 * payloadDataがはいっているものが、メディアデータとして必要なもの。
 * payLoadUnitStartIndicatorのフラグが立っている場合
 *  h.264の場合は、先頭の4バイトが0x000001E0になっている。
 *  次の3バイトにpacketLengthとmarkerBitがはいっている。(とりあえず無視してる)
 *  次の2ビットがPTSフラグとDTSフラグ(両方ないと、処理できないとする。)
 *  とりあえず、PTSは必須、DTSはなくてもなんとかなるっぽい。
 * 
 * @see http://en.wikipedia.org/wiki/Packetized_elementary_stream
 * @see http://en.wikipedia.org/wiki/Elementary_stream
 * 
 * @author taktod
 */
public class Pes extends Packet {
	private int packetStartCodePrefix; // 3バイト 0x000001固定
	private Bit8 streamId; // audioなら0xC0 - 0xDF videoなら0xE0 - 0xEF通常は0xC0もしくは0xE0(トラックが１つずつしかないため)
	private short pesPacketLength; // 2バイト
	private Bit2 markerBits; // 10固定
	private Bit2 scramblingControl;
	private Bit1 priority;
	private Bit1 dataAlignmentIndicator;
	private Bit1 copyright;
	private Bit1 originFlg;
	
	private Bit2 ptsDtsIndicator; // ここ・・・wikiによると11だとboth、1だとPTSのみと書いてあるけど10の間違いではないですかね。
	private Bit1 escrFlag;
	private Bit1 esRateFlag;
	private Bit1 DSMTrickModeFlag;
	private Bit1 additionalCopyInfoFlag;
	private Bit1 CRCFlag;
	private Bit1 extensionFlag;
	
	private Bit8 PESHeaederLength;

	// @see http://dvd.sourceforge.net/dvdinfo/pes-hdr.html
	// PTSDTSFlagがついている場合
	// 0010 PTS1 PTS. ...1 PTS. ...1
	
	// 0011 PTS1 PTS. ...1 PTS. ...1
	// 0001 DTS1 DTS. ...1 DTS. ...1
	
	// ESCR(elementaryStreamClockReference)
	// 00ES C1ES CR.. .1ES CR.. .1ES CRex t..1
	
	// ESRate
	// 1Esr ate. .... .... .... ...1
	
	// ここから先はh.264のnal構造やaacのadtsデータとかが続く。
	// 最終のパケットで余白が必要な場合adaptationFieldが挿入されてflagがすべてoffなデータが作られるみたい。
	// この場合内容データはすべて0xFFになっている模様。
	
	/** このエレメントがPcrであるか(時間同期用のパケットであるかどうか) */
	private final boolean pcrFlg;
	/** このエレメントのコーデック情報 */
	private final CodecType codec;
	/**
	 * コンストラクタ
	 * @param buffer
	 * @param codec
	 * @param pcrFlg
	 */
	public Pes(ByteBuffer buffer, CodecType codec, boolean pcrFlg) {
		this(0, buffer, codec, pcrFlg);
	}
	/**
	 * コンストラクタ
	 * @param position
	 * @param buffer
	 * @param codec
	 * @param pcrFlg
	 */
	public Pes(int position, ByteBuffer buffer, CodecType codec, boolean pcrFlg) {
		super(position, buffer);
		this.codec = codec;
		this.pcrFlg = pcrFlg;
	}
	/**
	 * pcrパケットであるか確認
	 * @return
	 */
	public boolean isPcr() {
		return pcrFlg;
	}
	/**
	 * codec情報を取得
	 * @return
	 */
	public CodecType getCodec() {
		return codec;
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		IReadChannel channel = new ByteReadChannel(getBuffer());
		analyzeHeader(channel);
	}
	@Override
	public String toString() {
		StringBuilder dump = new StringBuilder();
		dump.append("Es:");
		dump.append(" codec:").append(codec);
		dump.append(" pcr:").append(pcrFlg);
		return dump.toString();
	}
}
