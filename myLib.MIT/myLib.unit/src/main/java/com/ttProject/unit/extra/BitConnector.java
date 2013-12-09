package com.ttProject.unit.extra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * bitデータのコネクト処理
 * TODO 現状では、固定データをByteBufferにするのは対応しているが、追記しながら様子見つつというのはできてないね。
 * @author taktod
 */
public class BitConnector {
	/** feedしていくbitリスト */
	private List<Bit> bits = null;
	/**
	 * 接続します。
	 * @param bits
	 */
	public ByteBuffer connect(Bit... bits) {
		long data = 0;
		int left = 0;
		int size = 0;
		for(Bit bit : bits) {
			if(bit != null) {
				size += bit.bitCount;
			}
		}
		ByteBuffer buffer = ByteBuffer.allocate((int)(Math.ceil(size / 8.0D)));
		for(Bit bit : bits) {
			if(bit == null) {
				continue;
			}
			if(bit instanceof ExpGolomb) {
				ExpGolomb eg = (ExpGolomb) bit;
				for(Bit egBit : eg.bits) {
					// TODO この部分下と重複している。
					data = (data << egBit.bitCount) | egBit.get();
					left += egBit.bitCount;
					while(left > 8) {
						left -= 8;
						buffer.put((byte)((data >>> left) & 0xFF));
					}
				}
			}
			else if(bit instanceof BitN) {
				BitN bitN = (BitN)bit;
				for(Bit b : bitN.bits) {
					// TODO この部分下と重複している。
					data = (data << b.bitCount) | b.get();
					left += b.bitCount;
					while(left > 8) {
						left -= 8;
						buffer.put((byte)((data >>> left) & 0xFF));
					}
				}
			}
			else {
				data = (data << bit.bitCount) | bit.get();
				left += bit.bitCount;
				while(left > 8) {
					left -= 8;
					buffer.put((byte)((data >>> left) & 0xFF));
				}
			}
		}
		buffer.put((byte)((data >>> (8 - left)) & 0xFF));
		buffer.flip();
		return buffer;
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
