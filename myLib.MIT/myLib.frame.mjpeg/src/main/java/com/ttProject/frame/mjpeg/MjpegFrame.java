package com.ttProject.frame.mjpeg;

import java.nio.ByteBuffer;

import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mjpegのコーデックの映像の内容を解析します。
 * とりあえず必要のない情報を撤去して、必要のある情報を確定しておきたい。(縦横とか。)
 * @author taktod
 */
public class MjpegFrame extends VideoFrame {
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
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
