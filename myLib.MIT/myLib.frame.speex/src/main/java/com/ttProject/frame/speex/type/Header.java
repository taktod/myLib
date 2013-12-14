package com.ttProject.frame.speex.type;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;

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
public class Header extends SpeexFrame {
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
	public void fillWithFlvDefault(IReadChannel channel) throws Exception {
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
		super.setSampleNum(frameSize);
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setReadPosition(channel.position());
		super.setSize(channel.size());
		super.setSampleNum(320);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
