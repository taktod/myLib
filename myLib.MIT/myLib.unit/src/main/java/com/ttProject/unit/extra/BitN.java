package com.ttProject.unit.extra;

import java.util.ArrayList;
import java.util.List;

/**
 * 複数のbitを結合して使えるようにするbit
 * bit数は始めに定義したままとします。(入力数値によって可変ではありません)
 * TODO 32bit以上の場合のget,setはlongで扱うべき
 * @author taktod
 */
public class BitN extends Bit {
	/** 表現用の内部bit */
	protected final List<Bit> bits = new ArrayList<Bit>();
	/**
	 * コンストラクタ
	 * @param bits
	 */
	public BitN(Bit ... bits) {
		super(0);
		int count = 0;
		for(Bit bit : bits) {
			if(bit == null) {
				continue;
			}
			count += bit.bitCount;
			this.bits.add(bit);
		}
		bitCount = count;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get() {
		int value = 0;
		for(Bit bit : bits) {
			value <<= bit.bitCount;
			value |= bit.get();
		}
		return value;
	}
	/**
	 * long値でデータを参照します
	 * @return
	 */
	public long getLong() {
		long value = 0;
		for(Bit bit : bits) {
			value <<= bit.bitCount;
			value |= bit.get();
		}
		return value;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		int size = bits.size();
		for(int i = size - 1;i >= 0;i --) {
			Bit bit = bits.get(i);
			bit.set(value);
			value >>>= bit.bitCount;
		}
	}
	/**
	 * long値でデータを設定します
	 * @param value
	 */
	public void setLong(long value) {
		int size = bits.size();
		for(int i = size - 1;i >= 0;i --) {
			Bit bit = bits.get(i);
			bit.set((int)(value & 0xFFFFFFFF));
			value >>>= bit.bitCount;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		for(Bit b : bits) {
			data.append(b.toString());
		}
		return data.toString();
	}
}
