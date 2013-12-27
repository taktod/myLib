package com.ttProject.frame.speex.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

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
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	/** frameの内部データ */
	private ByteBuffer frameBuffer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		super.setSize(channel.size());
		// そのままデータを保持しておいておわり。
		frameBuffer = BufferUtil.safeRead(channel, channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameBufferがnullでした。先に解析してください。");
		}
		super.setData(frameBuffer);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return getData();
	}
	@Override
	public boolean isComplete() {
		return true;
	}
}
