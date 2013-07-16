package com.ttProject.media.mpegts;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttProject.media.Manager;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pat;
import com.ttProject.media.mpegts.packet.Pmt;
import com.ttProject.media.mpegts.packet.Sdt;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * mpegtsの内部データの解析を実行します。
 * patからpmtがわかる。
 * pmtからesがわかる。(コーデック情報もわかる)
 * といった感じになっている
 * @author taktod
 */
public class MpegtsManager extends Manager<Packet> {
	/** PatID(テーブルの基本情報) */
	private final int patId = 0x0000;
	/** SdtID(情報) */
	private final int sdtId = 0x0011;
	/** PmtID(Patによる) */
	private short pmtId;
	/** pcrとして、指定されているPID */
	private int pcrPid;
	/** elementStreamのデータ pic -> CodecTypeとしてある */
	private Map<Integer, CodecType> esMap = new HashMap<Integer, CodecType>();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Packet> getUnits(ByteBuffer data) throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Packet getUnit(IReadChannel source) throws Exception {
		// 188バイトデータがあるか確認する。
		if(source.size() - source.position() < 188) {
			// 188バイト未満なのでmpegtsのpacketとして成立しない。
			return null;
		}
		int position = source.position();
		ByteBuffer buffer = BufferUtil.safeRead(source, 188);
		// 先頭が0x47になっているか確認する。
		if(buffer.get() != 0x47) {
			throw new Exception("先頭が0x47になっていませんでした。mpegtsとして成立していません。");
		}
		int pid = getPid(buffer); // 2バイト読み込んである
		// pidによってなんのデータであるか分岐させておく必要がある
		if(pid == patId) {
			Pat pat = new Pat(position, buffer);
			// patを解析して、pmtを知る必要あり
			pat.analyze(source);
			pmtId = pat.getProgramPId();
			return pat;
		}
		else if(pid == sdtId) {
			Sdt sdt = new Sdt(position, buffer);
			// 外でほしかったら勝手にanalyzeすればいいと思う
			sdt.analyze(source);
			return sdt;
		}
		else if(pmtId == pid) {
			Pmt pmt = new Pmt(position, buffer);
			// pmtを解析して、esを知る必要あり。
			pmt.analyze(source);
			// pcr(時間情報を保持しているesのpid)
			pcrPid = pmt.getPcrPid();
			for(PmtElementaryField field : pmt.getFields()) {
				esMap.put((int)field.getPid(), field.getCodecType());
			}
			return pmt;
		}
		else if(esMap.containsKey(pid)) {
			// メディアデータ
			Pes es = new Pes(position, buffer, esMap.get(pid), pid == pcrPid);
			System.out.println("position:" + Integer.toHexString(position));
			es.analyze(source);
			return es;
		}
		else {
			// しらないデータ、これがきた場合は、なんとかしておかないとだめ
			System.out.print("unknownデータ:");
			System.out.println(pid);
			return null;
		}
	}
	/**
	 * pidを計算して出す
	 * @param buffer
	 * @return
	 */
	private int getPid(ByteBuffer buffer) {
		return buffer.getShort() & 0x1FFF;
	}
}
