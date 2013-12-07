package com.ttProject.container.flv;

import java.nio.ByteBuffer;

import com.ttProject.container.IContainer;
import com.ttProject.unit.extra.Bit1;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit8;
import com.ttProject.unit.extra.BitN.Bit24;
import com.ttProject.unit.extra.BitN.Bit32;

/**
 * flvのheader情報のtag
 * @author taktod
 */
public class FlvHeaderTag implements IContainer {
	private Bit24 signature;
	private Bit8 version;
	private Bit5 reserved1;
	private Bit1 audioFlags;
	private Bit1 reserved2;
	private Bit1 videoFlags;
	private Bit32 length;
	private Bit32 reserved3;
	public FlvHeaderTag(Bit24 signature) {
		this.signature = signature;
	}
	@Override
	public ByteBuffer getData() throws Exception {
		return null;
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
	public long getSize() {
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
}
