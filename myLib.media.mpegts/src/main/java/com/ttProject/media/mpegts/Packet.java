package com.ttProject.media.mpegts;

import java.nio.ByteBuffer;

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
	private ByteBuffer buffer;
	private final byte syncByte = 0x47;
	private Bit1 transportErrorIndicator;
	private Bit1 payloadUnitStartIndicator;
	private Bit1 transportPriority;
	private short pid; // 13bit
	private Bit2 scramblingControl;
	private Bit2 adaptationFieldExist;
	private Bit4 continuityCounter;
	
	/** 内包しているadaptationFieldの情報 */
	private AdaptationField adaptationField;
	/**
	 * コンストラクタ
	 * @param position
	 */
	public Packet(int position, ByteBuffer buffer) {
		// 大きさは188バイト固定
		super(position, 188);
		this.buffer = buffer.duplicate();
		this.buffer.position(0);
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
	/**
	 * データ実態を応答する
	 * @return
	 */
	public ByteBuffer getBuffer() {
		return buffer;
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
		adaptationFieldExist = new Bit2();
		continuityCounter = new Bit4();
		Bit.bitLoader(channel, syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority,
				pid_1, pid_2, scramblingControl, adaptationFieldExist, continuityCounter);
		if(syncByte.get() != this.syncByte) {
			throw new Exception("syncByteがおかしいです。");
		}
		pid = (short)((pid_1.get() << 8) + pid_2.get());
		System.out.println(dump());
		if(adaptationFieldExist.get() != 0x00) {
			// adaptationFieldがある場合
			adaptationField = new AdaptationField();
			adaptationField.analyze(channel);
		}
	}
	public String dump() {
		StringBuilder data = new StringBuilder("packet:");
		data.append(" tei:").append(transportErrorIndicator);
		data.append(" pusi:").append(payloadUnitStartIndicator);
		data.append(" tp:").append(transportPriority);
		data.append(" pid:").append(Integer.toHexString(pid));
		data.append(" sc:").append(scramblingControl);
		data.append(" afe:").append(adaptationFieldExist);
		data.append(" cc:").append(continuityCounter);
		return data.toString();
	}
}
