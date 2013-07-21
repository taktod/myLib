package com.ttProject.media.mpegts.field;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.CodecType;
import com.ttProject.nio.channels.IReadChannel;

/**
 * pmtのelementaryStreamのデータ記述部
 * @author taktod
 */
public class PmtElementaryField {
	private Bit8 streamType;
	private Bit3 reserved1;
	private short pid; // 13bit
	private Bit4 reserved2;
	private short esInfoLength; // 12bit
	// ESDescriptor
//	private Descriptor esDescriptor; // 形式がわからないので、とりあえず放置
	public int getSize() {
		return 5 + esInfoLength;
	}
	/**
	 * 対象pidを取得
	 * @return
	 */
	public short getPid() {
		return pid;
	}
	/**
	 * 対象コーデックタイプを取得
	 * @return
	 * @throws Exception
	 */
	public CodecType getCodecType() throws Exception {
		return CodecType.getType(streamType.get());
	}
	/**
	 * 解析しておく。
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IReadChannel ch) throws Exception {
		streamType = new Bit8();
		reserved1 = new Bit3();
		Bit5 pid_1 = new Bit5();
		Bit8 pid_2 = new Bit8();
		reserved2 = new Bit4();
		Bit4 esInfoLength_1 = new Bit4();
		Bit8 esInfoLength_2 = new Bit8();
		Bit.bitLoader(ch, streamType, reserved1, pid_1, pid_2, reserved2,
				esInfoLength_1, esInfoLength_2);
		pid = (short)((pid_1.get() << 8) | pid_2.get());
		esInfoLength = (short)((esInfoLength_1.get() << 8) | esInfoLength_2.get());
		if(esInfoLength != 0) {
			throw new Exception("elementaryStreamのdescriptorが定義されていました。作者に解析を依頼してください。");
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("  ");
		data.append("pmtElementrayField:");
		try {
			data.append(" type:").append(CodecType.getType(streamType.get()));
		}
		catch (Exception e) {
		}
		data.append(" r1:").append(reserved1);
		data.append(" pid:").append(Integer.toHexString(pid));
		data.append(" r2:").append(reserved2);
		data.append(" eil").append(Integer.toHexString(esInfoLength));
		return data.toString();
	}
}
