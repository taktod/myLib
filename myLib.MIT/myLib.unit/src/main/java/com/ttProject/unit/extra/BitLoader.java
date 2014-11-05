/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit64;
import com.ttProject.unit.extra.bit.Bit7;
import com.ttProject.util.BufferUtil;

/**
 * load bit data from ReadChannel
 * @author taktod
 */
public class BitLoader {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(BitLoader.class);
	/** targetChannel */
	private final IReadChannel channel;
	/** tmpData */
	private long tmpData = 0;
	/** left bit count */
	private int left = 0;
	/** little endian flag */
	private boolean littleEndianFlg = false;
	/** for h264 or h265 flag */
	private boolean emulationPreventionFlg = false;
	/** for h264 or h265 byte data for 0x00 */
	private byte firstByte = -1;
	private byte secondByte = -1;
	/**
	 * set little endian flag
	 * @param flg
	 */
	public void setLittleEndianFlg(boolean flg) {
		littleEndianFlg = flg;
	}
	/**
	 * check mode is little endian.
	 * @return
	 */
	public boolean isLittleEndian() {
		return littleEndianFlg;
	}
	/**
	 * for h264 or h265 emulation prevention flg
	 * @param flg
	 */
	public void setEmulationPreventionFlg(boolean flg) {
		emulationPreventionFlg = flg;
	}
	/**
	 * check mode is emulation prevention.
	 * @return
	 */
	public boolean isEmulationPrevention() {
		return emulationPreventionFlg;
	}
	/**
	 * constructor
	 * @param channel
	 */
	public BitLoader(IReadChannel channel) throws Exception {
		this.channel = channel;
	}
	/**
	 * load the bit.
	 * @param bit
	 */
	public void load(Bit bit) throws Exception {
		if(bit instanceof EbmlValue) {
			EbmlValue ebml = (EbmlValue) bit;
			Bit1 bit1 = null;
			do {
				bit1 = new Bit1();
				load(bit1);
			} while(ebml.addBit1(bit1)); // check the data size, by reading one by one.
			load(ebml.getDataBit()); // load the left data.
		}
		else if(bit instanceof ExpGolomb) {
			ExpGolomb golomb = (ExpGolomb) bit;
			Bit1 bit1 = null;
			do {
				bit1 = new Bit1();
				load(bit1);
			} while(golomb.addBit1(bit1));
		}
		else {
			if(littleEndianFlg) {
				while(left < bit.bitCount) {
					byte currentByte = BufferUtil.safeRead(channel, 1).get();
					if(emulationPreventionFlg) {
						if(firstByte == 0 && secondByte == 0 && currentByte == 3) {
							firstByte = -1;
							secondByte = -1;
							continue;
						}
						firstByte = secondByte;
						secondByte = currentByte;
					}
					tmpData = (tmpData | (currentByte & 0xFFL) << left);
					left += 8;
				}
				int bitCount = bit.bitCount;
				if(bit instanceof Bit64) {
					((Bit64)bit).setLong(tmpData);
				}
				else if(bit instanceof BitN) {
					((BitN) bit).setLong(tmpData & ((1L << bitCount) - 1));
				}
				else {
					bit.set((int)(tmpData & ((1L << bitCount) - 1)));
				}
				if(bitCount == 64) {
					// shift task for 64 bit is nothing. therefore, do 32bit shift twice.
					tmpData >>>= 32;
					tmpData >>>= 32;
				}
				else {
					tmpData >>>= bitCount;
				}
				left -= bitCount;
			}
			else {
				// TODO BitNを分割して読み込む動作がなくなったことで64bit以上のBitNデータが読み込み不能になっている。(nellymoserの読み込みでこまるはず)
				// support only big endian now.
				if(bit instanceof BitN && bit.bitCount > 64) {
					for(Bit b : ((BitN)bit).bits) {
						load(b);
					}
				}
				else {
					while(left < bit.bitCount) {
						byte currentByte = BufferUtil.safeRead(channel, 1).get();
						if(emulationPreventionFlg) {
							if(firstByte == 0 && secondByte == 0 && currentByte == 3) {
								firstByte = -1;
								secondByte = -1;
								continue;
							}
							firstByte = secondByte;
							secondByte = currentByte;
						}
						tmpData = (tmpData << 8 | (currentByte & 0xFFL));
						left += 8;
					}
					int bitCount = bit.bitCount;
					if(bit instanceof BitN) {
						((BitN) bit).setLong(tmpData >>> (left - bitCount));
					}
					else {
						bit.set((int)(tmpData >>> (left - bitCount)));
					}
					left -= bitCount;
				}
			}
		}
	}
	/**
	 * load multiple bits.
	 * @param bits
	 * @throws Exception
	 */
	public void load(Bit... bits) throws Exception {
		for(Bit bit : bits) {
			if(bit != null) {
				load(bit);
			}
		}
	}
	/**
	 * get the extra data(less than 1byte).
	 * @return
	 */
	public Bit getExtraBit() throws Exception {
		Bit bit = null;
		switch(left) {
		case 1:
			bit = new Bit1();
			break;
		case 2:
			bit = new Bit2();
			break;
		case 3:
			bit = new Bit3();
			break;
		case 4:
			bit = new Bit4();
			break;
		case 5:
			bit = new Bit5();
			break;
		case 6:
			bit = new Bit6();
			break;
		case 7:
			bit = new Bit7();
			break;
		default:
			return null;
		}
		load(bit);
		return bit;
	}
}
