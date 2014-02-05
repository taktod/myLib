package com.ttProject.unit.extra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * bitデータのコネクト処理
 * TODO 現状では、固定データをByteBufferにするのは対応しているが、追記しながら様子見つつというのはできてないね。
 * @author taktod
 */
public class BitConnector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(BitConnector.class);
	/** feedしていくbitリスト */
	private List<Bit> bits = null;
	/** エンディアンコントロール */
	private boolean littleEndianFlg = false;
	/** 内部処理用データ */
	private long data;
	private int left;
	private int size;
	private ByteBuffer buffer = null;
	/**
	 * 動作エンディアンをlittleEndianに変更する
	 * @param flg
	 */
	public void setLittleEndianFlg(boolean flg) {
		littleEndianFlg = flg;
	}
	/**
	 * littleEndianとして動作しているか確認
	 * @return
	 */
	public boolean isLittleEndian() {
		return littleEndianFlg;
	}
	/**
	 * 接続します。
	 * @param bits
	 */
	public ByteBuffer connect(Bit... bits) {
		data = 0;
		left = 0;
		size = 0;
		for(Bit bit : bits) {
			if(bit != null) {
				size += bit.bitCount;
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
			else if(bit instanceof Ebml) {
				Ebml ebml = (Ebml)bit;
				appendBit(ebml.getNumBit());
				appendBit(ebml.getDataBit());
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
	 * データの書き込み処理
	 */
	private void appendBit(Bit b) {
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
	 * byteデータの書き込み
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
	 * collectionFrameWorkの場合
	 * @param bits
	 * @return
	 */
	public ByteBuffer connect(List<Bit> bits) {
		return connect(bits.toArray(new Bit[]{}));
	}
	/**
	 * 追記していくデータ
	 * @param bits
	 */
	public void feed(List<Bit> bits) {
		if(this.bits == null) {
			this.bits = new ArrayList<Bit>();
		}
		this.bits.addAll(bits);
	}
	/**
	 * 追記していくデータ
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
	 * 追記したデータ接続
	 * @return
	 */
	public ByteBuffer connect() {
		if(bits == null) {
			return null;
		}
		return connect(bits);
	}
}
