package com.ttProject.container.flv;

import java.nio.ByteBuffer;

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
public class FlvHeaderTag implements IContainer {
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
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getData() throws Exception {
		BitConnector connector = new BitConnector();
		return connector.connect(signature, version, reserved1,
				audioFlag, reserved2, videoFlag, length, reserved3);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPosition() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return 13;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getPts() {
		return 0;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimebase() {
		return 1000;
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
	}
}
