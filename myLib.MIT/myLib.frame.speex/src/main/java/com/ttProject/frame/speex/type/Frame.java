package com.ttProject.frame.speex.type;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * speexのframe
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
 * speexを含むoggFileは
 * OggPage[headerFrame]
 * OggPage[commentFrame]
 * OggPage[frame,frame,frame,frame....]
 * OggPage[frame,frame,frame,frame....]
 * OggPage[frame,frame,frame,frame....]
 * という構成になっているみたい。
 * @author taktod
 */
public class Frame extends SpeexFrame {
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
