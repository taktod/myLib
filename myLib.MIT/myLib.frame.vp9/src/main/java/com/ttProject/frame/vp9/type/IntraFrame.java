package com.ttProject.frame.vp9.type;

import java.nio.ByteBuffer;

import com.ttProject.frame.vp9.Vp9Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.util.BufferUtil;

/**
 * vp9の中間フレーム
 * @author taktod
 */
public class IntraFrame extends Vp9Frame {
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param frameMarker
	 * @param profile
	 * @param reserved
	 * @param refFlag
	 * @param keyFrameFlag
	 * @param invisibleFlag
	 * @param errorRes
	 */
	public IntraFrame(Bit2 frameMarker, Bit1 profile, Bit1 reserved, Bit1 refFlag,
			Bit1 keyFrameFlag, Bit1 invisibleFlag, Bit1 errorRes) {
		super(frameMarker, profile, reserved, refFlag, keyFrameFlag, invisibleFlag, errorRes);
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setReadPosition(channel.position());
		setSize(channel.size());
		super.update();
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position();
		buffer = BufferUtil.safeRead(channel, getSize() - getReadPosition());
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(buffer == null) {
			throw new Exception("本体データが設定されていません");
		}
		setData(BufferUtil.connect(getHeaderBuffer(), buffer));
	}
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
//		return getData();
		throw new Exception("xuggle等での扱いが不明なので、とりあえず例外飛ばしておきます。");
	}
}

