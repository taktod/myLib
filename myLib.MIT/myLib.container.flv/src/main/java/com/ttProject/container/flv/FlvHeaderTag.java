package com.ttProject.container.flv;

import org.apache.log4j.Logger;

import com.ttProject.container.Container;
import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN.Bit24;
import com.ttProject.unit.extra.BitN.Bit32;

/**
 * flvのheader情報のtag
 * @author taktod
 */
public class FlvHeaderTag extends Container implements IContainer {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(FlvHeaderTag.class);
	private Bit24 signature = null;
	private Bit8 version = null;
	private Bit5 reserved1 = null;
	private Bit1 audioFlag = null;
	private Bit1 reserved2 = null;
	private Bit1 videoFlag = null;
	private Bit32 length = null;
	private Bit32 reserved3 = null;
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		// 全データを読み込んでしまう。
		BitLoader loader = new BitLoader(channel);
		version = new Bit8();
		reserved1 = new Bit5();
		audioFlag = new Bit1();
		reserved2 = new Bit1();
		videoFlag = new Bit1();
		length = new Bit32();
		reserved3 = new Bit32();
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
