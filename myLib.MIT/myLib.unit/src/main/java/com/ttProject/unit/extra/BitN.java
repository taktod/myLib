package com.ttProject.unit.extra;

import java.util.ArrayList;
import java.util.List;

/**
 * 複数のbitを結合して使えるようにするbit
 * bit数は始めに定義したままとします。(入力数値によって可変ではありません)
 * @author taktod
 */
public class BitN extends Bit {
	protected final List<Bit> bits = new ArrayList<Bit>();
	/**
	 * コンストラクタ
	 * @param bits
	 */
	public BitN(Bit ... bits) {
		super(0);
		int count = 0;
		for(Bit bit : bits) {
			count += bit.bitCount;
			this.bits.add(bit);
		}
		bitCount = count;
	}
	/**
	 * 数値で応答する
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
	@Override
	public void set(int value) {
		int size = bits.size();
		for(int i = size - 1;i >= 0;i --) {
			Bit bit = bits.get(i);
			bit.set(value);
			value >>>= bit.bitCount;
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		for(Bit b : bits) {
			data.append(b.toString());
		}
		return data.toString();
	}
	public class Bit9 extends BitN {}
	public class Bit10 extends BitN {}
	public class Bit11 extends BitN {}
	public class Bit12 extends BitN {}
	public class Bit13 extends BitN {}
	public class Bit14 extends BitN {}
	public class Bit15 extends BitN {}
	public class Bit16 extends BitN {}
	public class Bit17 extends BitN {}
	public class Bit18 extends BitN {}
	public class Bit19 extends BitN {}
	public class Bit20 extends BitN {}
	public class Bit21 extends BitN {}
	public class Bit22 extends BitN {}
	public class Bit23 extends BitN {}
	public class Bit24 extends BitN {}
	public class Bit25 extends BitN {}
	public class Bit26 extends BitN {}
	public class Bit27 extends BitN {}
	public class Bit28 extends BitN {}
	public class Bit29 extends BitN {}
	public class Bit30 extends BitN {}
	public class Bit31 extends BitN {}
	public class Bit32 extends BitN {}
	public class Bit33 extends BitN {}
	public class Bit34 extends BitN {}
	public class Bit35 extends BitN {}
	public class Bit36 extends BitN {}
	public class Bit37 extends BitN {}
	public class Bit38 extends BitN {}
	public class Bit39 extends BitN {}
	public class Bit40 extends BitN {}
	public class Bit41 extends BitN {}
	public class Bit42 extends BitN {}
	public class Bit43 extends BitN {}
	public class Bit44 extends BitN {}
	public class Bit45 extends BitN {}
	public class Bit46 extends BitN {}
	public class Bit47 extends BitN {}
	public class Bit48 extends BitN {}
	public class Bit49 extends BitN {}
	public class Bit50 extends BitN {}
	public class Bit51 extends BitN {}
	public class Bit52 extends BitN {}
	public class Bit53 extends BitN {}
	public class Bit54 extends BitN {}
	public class Bit55 extends BitN {}
	public class Bit56 extends BitN {}
	public class Bit57 extends BitN {}
	public class Bit58 extends BitN {}
	public class Bit59 extends BitN {}
	public class Bit60 extends BitN {}
	public class Bit61 extends BitN {}
	public class Bit62 extends BitN {}
	public class Bit63 extends BitN {}
	public class Bit64 extends BitN {}
}
