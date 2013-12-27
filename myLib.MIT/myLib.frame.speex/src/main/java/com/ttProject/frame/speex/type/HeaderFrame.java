package com.ttProject.frame.speex.type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * speexのheader情報
 * 
 * speexでは、headerの部分が欠如しているらしい。(ffmpegの出力より)
 * これは推測ですが、どうやらspeexのoggファイル化したときにでる、header部分が固定化されているために、削除状態になっている感じ。
 * aacのdeviceSpecificInfoが１つで固定なので、省略されている感じ。
 * よってframe数はaudioTagごとに固定されているみたいです。
 * その確認として、２つのaudioTagが合体しているaudioTagをつくって、再生したところ、はじめの音がこわれました。
 * 正解な気がします。
 * 
 * 以上とりあえず推測
 * speexは1つのframeあたり320samplesで動作している模様です。
 * 
 * flvでは存在しないが、speexのheaderがきちんとある。
 * oggにするとheader + meta + 実体みたいな感じになるみたいです。
 * @see http://www.speex.org/docs/manual/speex-manual/node8.html
 * headerの情報は
 * 8byte: speexString						Speex   
 * 20byte: speexVersion(超過分は0x00で埋め)	1.2rc1
 * 4byte: speexVersionId					01 00 00 00
 * 4byte: headerSize						50 00 00 00 ←これが0x50を示しているみたい
 * 4byte: rate								00 7D 00 00 ←0x7d00 32000
 * 4byte: mode								02 00 00 00
 * 4byte: modeBitstreamVersion				04 00 00 00
 * 4byte: nbChannels						02 00 00 00
 * 4byte: bitrate							A0 73 00 00 ←29600(31kbpsっぽいけど・・・あわないなんだろう)
 * 4byte: frameSize							80 02 00 00 ←0x280 640
 * 4byte: vbr								00 00 00 00
 * 4byte: framesPerPacket					01 00 00 00
 * 4byte: extraHeaders						00 00 00 00
 * 4byte: reserved1							00 00 00 00
 * 4byte: reserved2							00 00 00 00
 * 
 * @author taktod
 */
public class HeaderFrame extends SpeexFrame {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(HeaderFrame.class);
	// TODO こいつもoggと同じくbit表示にした法がいいのかな・・・ とりあえず面倒なので保留
	private String speexString;
	private String speexVersion;
	private int speexVersionId;
	private int headerSize;
	private int rate; // samplingRate
	private int mode;
	private int modeBitstreamVersion;
	private int nbChannels;
	private int bitRate;
	private int frameSize;
	private int vbr;
	private int framesPerPacket;
	private int extraHeaders;
	private int reserved1;
	private int reserved2;
	/**
	 * flvのデフォルトデータですべて初期化する
	 */
	public void fillWithFlvDefault() throws Exception {
		speexString = "Speex   ";
		speexVersion = "1.2rc1";
		speexVersionId = 1;
		headerSize = 0x50;
		rate = 0x3E80;
		mode = 1;
		modeBitstreamVersion = 4;
		nbChannels = 1;
		bitRate = 0x6C98; // flvにあわせて変更すべき？
		frameSize = 0x140; // 固定っぽい
		vbr = 0;
		framesPerPacket = 1;
		extraHeaders = 0;
		reserved1 = 0;
		reserved2 = 0;
//		super.setReadPosition(channel.position());
		super.setSize(headerSize);
		super.setSampleNum(frameSize);
		super.setSampleRate(rate);
		super.setChannel(nbChannels);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		speexString = new String(BufferUtil.safeRead(channel, 8).array());
		speexVersion = new String(BufferUtil.safeRead(channel, 20).array());
		ByteBuffer buffer = BufferUtil.safeRead(channel, 52);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		speexVersionId = buffer.getInt();
		headerSize = buffer.getInt();
		rate = buffer.getInt();
		mode = buffer.getInt();
		modeBitstreamVersion = buffer.getInt();
		nbChannels = buffer.getInt();
		bitRate = buffer.getInt();
		frameSize = buffer.getInt(); // sampleNumのことっぽい 320固定だとおもってたけど・・・
		vbr = buffer.getInt();
		framesPerPacket = buffer.getInt(); // これが1以外になったことはないから、sampleNumに影響あるんだろうか？
		extraHeaders = buffer.getInt();
		reserved1 = buffer.getInt();
		reserved2 = buffer.getInt();
//		logger.info(rate);
//		logger.info(frameSize);
//		logger.info(framesPerPacket);
		// ここでデータの読み込みを実行する。
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
		super.setSampleNum(frameSize);
		super.setSampleRate(rate);
		super.setChannel(nbChannels);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// 読み込むデータは特になし。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// データを結合してByteBufferを登録しておきたい。
		ByteBuffer buffer = ByteBuffer.allocate(80);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(speexString.getBytes());
		while(buffer.position() < 8) {
			buffer.put((byte)0x00);
		}
		buffer.put(speexVersion.getBytes());
		while(buffer.position() < 28) {
			buffer.put((byte)0x00);
		}
		buffer.putInt(speexVersionId);
		buffer.putInt(headerSize);
		buffer.putInt(rate);
		buffer.putInt(mode);
		buffer.putInt(modeBitstreamVersion);
		buffer.putInt(nbChannels);
		buffer.putInt(bitRate);
		buffer.putInt(frameSize);
		buffer.putInt(vbr);
		buffer.putInt(framesPerPacket);
		buffer.putInt(extraHeaders);
		buffer.putInt(reserved1);
		buffer.putInt(reserved2);
		buffer.flip();
		super.setData(buffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
	@Override
	public boolean isComplete() {
		return true;
	}
}
