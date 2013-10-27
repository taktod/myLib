package com.ttProject.packet.mpegts;

import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.field.PmtElementaryField;
import com.ttProject.media.mpegts.packet.Pes;

public abstract class MediaData {
	/** 動作pid保持 */
	private short pid;
	/** コーデック情報保持 */
	private CodecType type = null;
	/**
	 * pmtから必要な情報を取り出しておく。
	 * @param pmt
	 */
	public void analyzePmt(PmtElementaryField field) throws Exception {
		pid = field.getPid();
		type = field.getCodecType();
	}
	public abstract void analyzePes(Pes pes);
	/**
	 * pesのデータを解析する。
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
}
