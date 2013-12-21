package com.ttProject.container.ogg.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.ogg.OggPage;
import com.ttProject.frame.speex.SpeexFrameAnalyzer;
import com.ttProject.frame.theora.TheoraFrameAnalyzer;
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * startPage(speexとかのheader情報がはいっているっぽい。)
 * @author taktod
 */
public class StartPage extends OggPage {
	/** ロガー */
	private Logger logger = Logger.getLogger(StartPage.class);
	/** 解析プログラム */
	private IAnalyzer analyzer = null;
	private List<IUnit> frameList = new ArrayList<IUnit>();
	/**
	 * コンストラクタ
	 * @param version
	 * @param zeroFill
	 * @param logicEndFlag
	 * @param logicStartFlag
	 * @param packetContinurousFlag
	 */
	public StartPage(Bit8 version, Bit5 zeroFill,
			Bit1 logicEndFlag, Bit1 logicStartFlag,
			Bit1 packetContinurousFlag) {
		super(version, zeroFill, logicEndFlag, logicStartFlag, packetContinurousFlag);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		logger.info("minimumload");
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		boolean isFirstData = true;
		channel.position(getPosition() + 27 + getSegmentSizeList().size());
		for(Bit8 size : getSegmentSizeList()) {
			ByteBuffer buffer = BufferUtil.safeRead(channel, size.get());
			if(isFirstData) {
				// はじめのデータの場合はコーデックheaderがあると思われるので、なんのコーデックか判定する必要あり。
				switch(buffer.get()) {
				case 0x01:
					logger.info("vorbis?");
					analyzer = new VorbisFrameAnalyzer();
					break;
				case 'S':
					logger.info("speex?");
					analyzer = new SpeexFrameAnalyzer();
					break;
					// Theoraは？
				case (byte)0x80:
					logger.info("theora?");
					analyzer = new TheoraFrameAnalyzer();
					break;
				default:
					throw new Exception("知らないコーデックデータを検知しました。");
				}
				buffer.position(0);
			}
			isFirstData = false;
			IReadChannel bufferChannel = new ByteReadChannel(buffer);
			frameList.add(analyzer.analyze(bufferChannel));
			// bufferChannelの中身がなくなるまで読み込ませる必要あり。
		}
		// データを1byte読み込んで調べてみる。
		// vorbisなら0x01がくるはず 0x01 [vorbis]...
		// speexなら'S'がくるはず [Speex   ]
		// あたりをつけて残りのデータを読み込んで決定したい。
		// 次の位置に強制割り当てしている
		channel.position(getPosition() + getSize());
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
	/**
	 * 解析analyzer参照
	 * @return
	 */
	public IAnalyzer getAnalyzer() {
		return analyzer;
	}
}