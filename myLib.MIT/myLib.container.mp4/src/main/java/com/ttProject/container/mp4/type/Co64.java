package com.ttProject.container.mp4.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * co64の定義
 * @author taktod
 */
public class Co64 extends Mp4Atom {
	/** ロガー */
	private Logger logger = Logger.getLogger(Co64.class);
	private Bit8  version = new Bit8();
	private Bit24 flags   = new Bit24();
	private Bit32 count = null;
	private ByteBuffer buffer = null;
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Co64(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Co64() {
		super(new Bit32(), Type.getTypeBit(Type.Co64));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(version, flags);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		count = new Bit32();
		BitLoader loader = new BitLoader(channel);
		loader.load(count);
		buffer = BufferUtil.safeRead(channel, count.get() * 8);
		logger.info("prePos:" + channel.position());
		super.load(channel);
		logger.info("postPos:" + channel.position());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
