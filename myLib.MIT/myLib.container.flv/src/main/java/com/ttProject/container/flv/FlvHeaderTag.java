package com.ttProject.container.flv;

import org.apache.log4j.Logger;

import com.ttProject.container.Container;
import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * flvのheader情報のtag
 * @author taktod
 */
public class FlvHeaderTag extends Container implements IContainer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FlvHeaderTag.class);
	private final Bit24 signature;
	private Bit8 version = new Bit8(1);
	private Bit5 reserved1 = new Bit5();
	private Bit1 audioFlag = new Bit1();
	private Bit1 reserved2 = new Bit1();
	private Bit1 videoFlag = new Bit1();
	private Bit32 length = new Bit32(9);
	private Bit32 reserved3 = new Bit32();
	/**
	 * コンストラクタ
	 * @param signature
	 */
	public FlvHeaderTag(Bit24 signature) {
		this.signature = signature;
		setPosition(0);
		setPts(0);
		setTimebase(1000);
	}
	public FlvHeaderTag() {
		this(new Bit24('F' << 16 | 'L' << 8 | 'V'));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(reserved3 == null) {
			minimumLoad(channel);
		}
		// 全体で追加読み込みしないといけないデータはないものとします。
	}
	public void setVideoFlag(boolean flag) {
		videoFlag.set(flag ? 1 : 0);
	}
	public void setAudioFlag(boolean flag) {
		audioFlag.set(flag ? 1 : 0);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// 全データを読み込んでしまう。
		BitLoader loader = new BitLoader(channel);
		loader.load(version, reserved1, audioFlag, reserved2, videoFlag,
				length, reserved3);
		update();
		setSize(13);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		setData(connector.connect(signature, version, reserved1,
				audioFlag, reserved2, videoFlag, length, reserved3));
	}
}
