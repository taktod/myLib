package com.ttProject.container.mpegts;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.type.Pat;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.container.mpegts.type.Sdt;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.aac.AacFrameAnalyzer;
import com.ttProject.frame.h264.H264FrameAnalyzer;
import com.ttProject.frame.mp3.Mp3FrameAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * mpegtsのpacketを解析します
 * @author taktod
 */
public class MpegtsPacketSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(MpegtsPacketSelector.class);
	private final int patPid = 0x0000;
	private final int sdtPid = 0x0011;
	private Sdt sdt = null;
	private Pat pat = null;
	private Pmt pmt = null;
	private Map<Integer, Pes> pesMap = new HashMap<Integer, Pes>();
	private Map<Integer, IAnalyzer> analyzerMap = new HashMap<Integer, IAnalyzer>();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// headerの4byteを読み込めばどのtypeか解析ができるので、その4byteを読み込むようにする。
		if(channel.size() == channel.position()) {
			// データが最後まできているので処理することができない。
			return null;
		}
		Bit8 syncByte = new Bit8();
		Bit1 transportErrorIndicator = new Bit1();
		Bit1 payloadUnitStartIndicator = new Bit1();
		Bit1 transportPriority = new Bit1();
		Bit13 pid = new Bit13();
		Bit2 scramblingControl = new Bit2();
		Bit1 adaptationFieldExist = new Bit1();
		Bit1 payloadFieldExist = new Bit1();
		Bit4 continuityCounter = new Bit4();
		BitLoader loader = new BitLoader(channel);
		loader.load(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
		if(syncByte.get() != 0x47) {
			throw new Exception("syncBitが一致しません。");
		}
		MpegtsPacket packet = null;
		if(pid.get() == sdtPid) {
			Sdt tmpSdt = new Sdt(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
			tmpSdt.minimumLoad(channel);
			if(sdt == null || sdt.getCrc() != tmpSdt.getCrc()) {
				// sdtがない場合とcrc32が一致しない場合は、上書きしてそちらをloadにまわす。
				sdt = tmpSdt;
			}
			return sdt;
		}
		else if(pid.get() == patPid) {
			// patを保持しておく
			Pat tmpPat = new Pat(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
			tmpPat.minimumLoad(channel);
			if(pat == null || pat.getCrc() != tmpPat.getCrc()) {
				pat = tmpPat;
			}
			return pat;
		}
		else if(pat != null && pid.get() == pat.getPmtPid()){
			Pmt tmpPmt = new Pmt(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter);
			tmpPmt.minimumLoad(channel);
			if(pmt == null || pmt.getCrc() != tmpPmt.getCrc()) {
				pmt = tmpPmt;
			}
			else {
				return pmt;
			}
			// elementaryFieldを初期化するために、先にloadを実施してしまいます。
			pmt.load(channel);
			// pmtの解析がおわったら必要なanalyzerをつくらないとだめ
//			pmt.minimumLoad(channel);
			// analyzerをつくっておく。
			for(PmtElementaryField elementaryField : pmt.getFields()) {
				logger.info(elementaryField.getCodecType());
				switch(elementaryField.getCodecType()) {
				case AUDIO_AAC:
					analyzerMap.put((int)elementaryField.getPid(), new AacFrameAnalyzer());
					break;
				case AUDIO_MPEG1:
					analyzerMap.put((int)elementaryField.getPid(), new Mp3FrameAnalyzer());
					break;
				case VIDEO_H264:
					analyzerMap.put((int)elementaryField.getPid(), new H264FrameAnalyzer());
					break;
				default:
					break;
				}
			}
			return pmt;
		}
		else if(pmt != null && pmt.isPesPid(pid.get())) {
			Pes pes = new Pes(syncByte, transportErrorIndicator, payloadUnitStartIndicator, transportPriority, pid, scramblingControl, adaptationFieldExist, payloadFieldExist, continuityCounter, pmt.getPcrPid() == pid.get());
			// payLoadの開始位置でなかったら、startUnitのpesを取得して保持しなければならない。
			// TODO ここには２つほど問題がある。
			/*
			 * １つ目は、pesのデータ読み込み時に、frame用のbufferにデータをいれていくが、このデータがどういったサイズになるかは、65536byte以上の場合には、実際に読み込まないとわからない。(まぁこれはどうとでもなる。)
			 * ２つ目は、loadを実行できないこと。loadを実行しないと、frameデータが取得できない状態になっているが、selectorではminimumLoadのみ実行するように設計されているので、loadが実行されるかわからない。
			 * よって、reader側に応答は返さないけど、中途なデータであることを返さないとだめという中途半端なデータになってしまう。
			 * つまりselectorはpacketデータを解析して、応答したいように応答すればよい。
			 * reader側でどういう返し方をすると、frameが取り出しやすいか考えるようにすればいいか？
			 */
			if(payloadUnitStartIndicator.get() == 1) {
				// 前のデータがある場合はそれを応答しなければならない
				// unitの開始位置なので、pesを記録しておく。
				pesMap.put(pid.get(), pes);
				pes.setFrameAnalyzer(analyzerMap.get(pid.get()));
			}
			else {
				pes.setUnitStartPes(pesMap.get(pid.get()));
				// 中途データの場合は、NullUnitを応答しておく必要がある。
			}
			packet = pes;
		}
		else {
			logger.info("その他データ" + Integer.toHexString(pid.get()));
			// esPidであるか確認
			// その他(よくわからないのでスルーする)
			return null;
		}
		packet.minimumLoad(channel);
		return packet;
	}
}
