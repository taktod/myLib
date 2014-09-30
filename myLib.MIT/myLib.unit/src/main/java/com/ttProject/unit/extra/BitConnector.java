/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * connect for bit data.
 * @author taktod
 */
public class BitConnector {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(BitConnector.class);
	/** bit list for feeding */
	private List<Bit> bits = null;
	/** flag for little endian */
	private boolean littleEndianFlg = false;
	/** for data constructing */
	private long data;
	private int left;
	private int size;
	private ByteBuffer buffer = null;
	/**
	 * set the endian flag
	 * @param flg true:little endian false:big endian
	 */
	public void setLittleEndianFlg(boolean flg) {
		littleEndianFlg = flg;
	}
	/**
	 * is little endian working?
	 * @return
	 */
	public boolean isLittleEndian() {
		return littleEndianFlg;
	}
	/**
	 * connect
	 * @param bits
	 */
	public ByteBuffer connect(Bit... bits) {
		data = 0;
		left = 0;
		size = 0;
		for(Bit bit : bits) {
			if(bit != null) {
				size += bit.getBitCount();
			}
		}
		buffer = ByteBuffer.allocate((int)(Math.ceil(size / 8.0D)));
		for(Bit bit : bits) {
			if(bit == null) {
				continue;
			}
			if(bit instanceof ExpGolomb) {
				ExpGolomb eg = (ExpGolomb) bit;
				for(Bit egBit : eg.bits) {
					appendBit(egBit);
				}
			}
			else if(bit instanceof EbmlValue) {
				EbmlValue ebml = (EbmlValue)bit;
				appendBit(ebml.getEbmlNumBit());
				Bit dataBit = ebml.getEbmlDataBit();
				if(dataBit instanceof BitN) {
					BitN bitN = (BitN)dataBit;
					if(littleEndianFlg) {
						for(int i = bitN.bits.size() - 1;i >= 0;i --){
							appendBit(bitN.bits.get(i));
						}
					}
					else {
						for(Bit b : bitN.bits) {
							appendBit(b);
						}
					}
				}
				else {
					appendBit(dataBit);
				}
			}
			else if(bit instanceof BitN) {
				BitN bitN = (BitN)bit;
				if(littleEndianFlg) {
					for(int i = bitN.bits.size() - 1;i >= 0;i --){
						appendBit(bitN.bits.get(i));
					}
				}
				else {
					for(Bit b : bitN.bits) {
						appendBit(b);
					}
				}
			}
			else {
				appendBit(bit);
			}
		}
		if(buffer.position() != buffer.limit()) {
			if(littleEndianFlg) {
				writeBuffer(8 - left);
			}
			else {
				if(left != 0) {
					data <<= (8 - left);
				}
				writeBuffer(0);
			}
		}
		buffer.flip();
		return buffer;
	}
	/**
	 * append next bit
	 */
	private void appendBit(Bit b) {
		// if this bit is BitN, this must be trouble.
		if(littleEndianFlg) {
			data = data | (b.get() << left);
			left += b.bitCount;
			while(left >= 8) {
				left -= 8;
				writeBuffer(0);
				data >>>= 8;
			}
		}
		else {
			data = (data << b.bitCount) | b.get();
			left += b.bitCount;
			while(left >= 8) {
				left -= 8;
				writeBuffer(left);
			}
		}
	}
	/**
	 * put the data into buffer.
	 * @param shift
	 */
	private void writeBuffer(int shift) {
		if(littleEndianFlg) {
			buffer.put((byte)(data & 0xFF));
		}
		else {
			buffer.put((byte)((data >>> shift) & 0xFF));
		}
	}
	/**
	 * connect with the data of java.util.List
	 * @param bits
	 * @return
	 */
	public ByteBuffer connect(List<Bit> bits) {
		return connect(bits.toArray(new Bit[]{}));
	}
	/**
	 * add feeding data
	 * @param bits
	 */
	public void feed(List<Bit> bits) {
		if(this.bits == null) {
			this.bits = new ArrayList<Bit>();
		}
		this.bits.addAll(bits);
	}
	/**
	 * add feeding data
	 * @param bits
	 */
	public void feed(Bit ... bits) {
		if(this.bits == null) {
			this.bits = new ArrayList<Bit>();
		}
		for(Bit bit : bits) {
			this.bits.add(bit);
		}
	}
	/**
	 * connect for feeding data.
	 * @return
	 */
	public ByteBuffer connect() {
		if(bits == null) {
			return null;
		}
		return connect(bits);
	}
}
