package com.ttProject.packet.mpegts;

import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.media.mpegts.packet.Pmt;

public abstract class MediaData {
	/** 動作pid保持 */
	private short pid;
	/** コーデック情報保持 */
	private CodecType type = null;
	/** pcrであるかどうかフラグ */
	private boolean pcrFlg = false;
	/**
	 * pmtから必要な情報を取り出しておく。
	 * @param pmt
	 */
	public void analyzePmt(Pmt pmt, PmtElementaryField field) throws Exception {
		pid = field.getPid();
		type = field.getCodecType();
		pcrFlg = (pid == pmt.getPcrPid());
	}
	/**
	 * pcrであるかの判定
	 * @return
	 */
	public boolean isPcr() {
		return pcrFlg;
	}
	/**
	 * 対応pid参照
	 * @return
	 */
	public short getPid() {
		return pid;
	}
	/**
	 * コーデック情報参照
	 * @return
	 */
	public CodecType getCodecType() {
		return type;
	}
	/**
	 * pesデータを解析します。
	 * @param pes
	 */
	public abstract void analyzePes(Pes pes);
	/**
	 * pesのデータを確認する
	 * @param pes
	 */
	protected boolean checkPes(Pes pes) {
		if(pes.getPid() != pid) {
			return false;
		}
		if(type == null) {
			return false;
		}
		return true;
	}
	/**
	 * stackしているデータのpts値を応答します。
	 * @return
	 */
	public abstract long getStackedDataPts();
}
