package com.ttProject.frame.opus.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.opus.OpusFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * コメントフレーム(oggにはあるっぽい。なくてもいいのかも？)
 * とりあえずoggにはあるけど、webmにはなかった。
 * OpusTags
 * venderLength:
 * venderData
 * elementNum
 * Data1Length
 * Data1Data
 * Data2Length
 * Data2Data
 * ....
 * webm(mkv) -> ogg変換したかったら適当にねつ造したほうがよさそう。
 * @author taktod
 */
public class CommentFrame extends OpusFrame {
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
	public boolean isComplete() {
		return false;
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
