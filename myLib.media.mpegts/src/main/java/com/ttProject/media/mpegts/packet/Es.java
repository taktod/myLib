package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.Packet;

/**
 * ElementaryStreamPacket
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
public class Es extends Packet {
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
	public Es(ByteBuffer buffer, CodecType codec, boolean pcrFlg) {
		this(0, buffer, codec, pcrFlg);
	}
	/**
	 * コンストラクタ
	 * @param position
	 * @param buffer
	 * @param codec
	 * @param pcrFlg
	 */
	public Es(int position, ByteBuffer buffer, CodecType codec, boolean pcrFlg) {
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
	public String toString() {
		StringBuilder dump = new StringBuilder();
		dump.append("Es:");
		dump.append(" codec:").append(codec);
		dump.append(" pcr:").append(pcrFlg);
		return dump.toString();
	}
}
