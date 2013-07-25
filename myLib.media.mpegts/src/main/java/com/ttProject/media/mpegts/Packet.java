package com.ttProject.media.mpegts;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit5;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.field.AdaptationField;
import com.ttProject.nio.channels.IReadChannel;

/**
 * パケットデータのベース
 * @author taktod
 */
public abstract class Packet extends Unit {
	// 実データ(readModeではいっているものとします。)
	// packetがかならずbufferをもっているとすると、ちょっと扱いにくいので、bufferがきたら読み込みを実行することとした方がよさそう。
	private final byte syncByte = 0x47;
	private Bit1 transportErrorIndicator; // 0
	private Bit1 payloadUnitStartIndicator;
	private Bit1 transportPriority; // 0
	private short pid; // 13bit
	private Bit2 scramblingControl; // 0
	private Bit1 adaptationFieldExist;
	private Bit1 payloadFieldExist; // 1
	private Bit4 continuityCounter;
	
	/** 内包しているadaptationFieldの情報 */
	private AdaptationField adaptationField;
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Packet(int position) {
		// 大きさは188バイト固定
		super(position, 188);
	}
	/**
	 * 解析動作
	 */
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
	public boolean isPayloadUnitStart() {
		return payloadUnitStartIndicator.get() != 0x00;
	}
	protected void setPayloadUnitStartIndicator(int flg) {
		payloadUnitStartIndicator = new Bit1(flg);
	}
	protected void setAdaptationFieldExist(int flg) {
		adaptationFieldExist = new Bit1(flg);
	}
	public boolean isAdaptationFieldExist() {
		return adaptationFieldExist.get() != 0x00;
	}
	public short getPid() {
		return pid;
	}
	protected void setContinuityCounter(int counter) {
		continuityCounter = new Bit4(counter);
	}
	/**
	 * header部の解析を実施しておく
	 * @throws Exception
	 */
	protected void analyzeHeader(IReadChannel channel) throws Exception {
		// headerを解析しておきます。
		Bit8 syncByte = new Bit8();
		transportErrorIndicator = new Bit1();
		payloadUnitStartIndicator = new Bit1();
		transportPriority = new Bit1();
		Bit5 pid_1 = new Bit5();
		Bit8 pid_2 = new Bit8();
		scramblingControl = new Bit2();
		adaptationFieldExist = new Bit1();
		payloadFieldExist = new Bit1();
		continuityCounter = new Bit4();
		Bit.bitLoader(channel, syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority,
				pid_1, pid_2, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
		if(syncByte.get() != this.syncByte) {
			throw new Exception("syncByteがおかしいです。");
		}
		pid = (short)((pid_1.get() << 8) + pid_2.get());
		adaptationField = new AdaptationField();
		if(adaptationFieldExist.get() != 0x00) {
			// adaptationFieldがある場合
			adaptationField.analyze(channel);
		}
	}
	public AdaptationField getAdaptationField() {
		return adaptationField;
	}
	public void setAdaptationField(AdaptationField field) {
		adaptationField = field;
	}
	/**
	 * デフォルトの設定をつくっておく。
	 */
	public abstract void setupDefault() throws Exception;
	public abstract ByteBuffer getBuffer() throws Exception;
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(new Bit8(syncByte));
		list.add(transportErrorIndicator);
		list.add(payloadUnitStartIndicator);
		list.add(transportPriority);
		list.add(new Bit5(pid >>> 8));
		list.add(new Bit8(pid));
		list.add(scramblingControl);
		list.add(adaptationFieldExist);
		list.add(payloadFieldExist);
		list.add(continuityCounter);
		if(adaptationFieldExist.get() != 0x00) {
			list.addAll(adaptationField.getBits());
		}
		return list;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(" ");
		data.append("packet:");
		data.append(" tei:").append(transportErrorIndicator);
		data.append(" pusi:").append(payloadUnitStartIndicator);
		data.append(" tp:").append(transportPriority);
		data.append(" pid:").append(Integer.toHexString(pid));
		data.append(" sc:").append(scramblingControl);
		data.append(" afe:").append(adaptationFieldExist);
		data.append(" pfe:").append(payloadFieldExist);
		data.append(" cc:").append(continuityCounter);
		if(adaptationField != null) {
			data.append("\n");
			data.append(adaptationField);
		}
		data.append("\n");
		return data.toString();
	}
}
