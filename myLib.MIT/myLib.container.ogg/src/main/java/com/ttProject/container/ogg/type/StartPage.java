package com.ttProject.container.ogg.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.ttProject.container.ogg.Crc32;
import com.ttProject.container.ogg.OggPage;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.speex.SpeexFrameAnalyzer;
import com.ttProject.frame.theora.TheoraFrameAnalyzer;
import com.ttProject.frame.vorbis.VorbisFrameAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * startPage(speexとかのheader情報がはいっているっぽい。)
 * @author taktod
 * 
 * TODO Out of Memoryが発生する可能性があるので、frameListをpageごとに保持するように変更したほうがよい。
 * crc32の計算が微妙・・・どうすりゃいいんだ。
 */
public class StartPage extends OggPage {
	/** ロガー */
	private Logger logger = Logger.getLogger(StartPage.class);
	/** 解析プログラム */
	private IAnalyzer analyzer = null;
	/** 経過tic情報 */
	private long passedTic = 0;
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
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
			getFrameList().add((IFrame)analyzer.analyze(bufferChannel));
			// bufferChannelの中身がなくなるまで読み込ませる必要あり。
		}
		// データを1byte読み込んで調べてみる。
		// vorbisなら0x01がくるはず 0x01 [vorbis]...
		// speexなら'S'がくるはず [Speex   ]
		// あたりをつけて残りのデータを読み込んで決定したい。
		// 次の位置に強制割り当てしている
		channel.position(getPosition() + getSize());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// データをupdateしなければいけない。
		// ここでするべきことは、bufferをつくること。
		// headerBufferを書き込み
		ByteBuffer headerBuffer = getHeaderBuffer();
		ByteBuffer buffer = ByteBuffer.allocate(getSize());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(headerBuffer);
		// frameを書き込み
		for(IFrame frame : getFrameList()) {
			buffer.put(frame.getData());
		}
		ByteBuffer tmpBuffer = buffer.duplicate();
		tmpBuffer.flip();
		// crc32を作成して
		Crc32 crc32 = new Crc32();
		while(tmpBuffer.remaining() > 0) {
			crc32.update(tmpBuffer.get());
		}
		// crc32を更新する。
		buffer.position(22);
		buffer.putInt((int)crc32.getValue());
		buffer.position(tmpBuffer.position());
		buffer.flip();
		// おわり
		setData(buffer);
	}
	/**
	 * 解析analyzer参照
	 * @return
	 */
	public IAnalyzer getAnalyzer() {
		return analyzer;
	}
	/**
	 * 経過ticを設定する
	 * @param passedTic
	 */
	public void setPassedTic(long passedTic) {
		this.passedTic = passedTic;
	}
	/**
	 * 経過ticを参照する
	 * @return
	 */
	public long getPassedTic() {
		return passedTic;
	}
}
