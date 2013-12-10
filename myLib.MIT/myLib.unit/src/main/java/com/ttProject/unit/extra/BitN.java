package com.ttProject.unit.extra;

import java.util.ArrayList;
import java.util.List;

/**
 * 複数のbitを結合して使えるようにするbit
 * bit数は始めに定義したままとします。(入力数値によって可変ではありません)
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
	// 以下各々のbit数の設定
	public static class Bit1  extends com.ttProject.unit.extra.bit.Bit1 {};
	public static class Bit2  extends com.ttProject.unit.extra.bit.Bit2 {};
	public static class Bit3  extends com.ttProject.unit.extra.bit.Bit3 {};
	public static class Bit4  extends com.ttProject.unit.extra.bit.Bit4 {};
	public static class Bit5  extends com.ttProject.unit.extra.bit.Bit5 {};
	public static class Bit6  extends com.ttProject.unit.extra.bit.Bit6 {};
	public static class Bit7  extends com.ttProject.unit.extra.bit.Bit7 {};
	public static class Bit8  extends com.ttProject.unit.extra.bit.Bit8 {};
	public static class Bit9  extends BitN {public Bit9(int value)  {this();set(value);}public Bit9()  {super(new Bit1(), new Bit8());}}
	public static class Bit10 extends BitN {public Bit10(int value) {this();set(value);}public Bit10() {super(new Bit2(), new Bit8());}}
	public static class Bit11 extends BitN {public Bit11(int value) {this();set(value);}public Bit11() {super(new Bit3(), new Bit8());}}
	public static class Bit12 extends BitN {public Bit12(int value) {this();set(value);}public Bit12() {super(new Bit4(), new Bit8());}}
	public static class Bit13 extends BitN {public Bit13(int value) {this();set(value);}public Bit13() {super(new Bit5(), new Bit8());}}
	public static class Bit14 extends BitN {public Bit14(int value) {this();set(value);}public Bit14() {super(new Bit6(), new Bit8());}}
	public static class Bit15 extends BitN {public Bit15(int value) {this();set(value);}public Bit15() {super(new Bit7(), new Bit8());}}
	public static class Bit16 extends BitN {public Bit16(int value) {this();set(value);}public Bit16() {super(new Bit8(), new Bit8());}}
	public static class Bit17 extends BitN {public Bit17(int value) {this();set(value);}public Bit17() {super(new Bit1(), new Bit8(), new Bit8());}}
	public static class Bit18 extends BitN {public Bit18(int value) {this();set(value);}public Bit18() {super(new Bit2(), new Bit8(), new Bit8());}}
	public static class Bit19 extends BitN {public Bit19(int value) {this();set(value);}public Bit19() {super(new Bit3(), new Bit8(), new Bit8());}}
	public static class Bit20 extends BitN {public Bit20(int value) {this();set(value);}public Bit20() {super(new Bit4(), new Bit8(), new Bit8());}}
	public static class Bit21 extends BitN {public Bit21(int value) {this();set(value);}public Bit21() {super(new Bit5(), new Bit8(), new Bit8());}}
	public static class Bit22 extends BitN {public Bit22(int value) {this();set(value);}public Bit22() {super(new Bit6(), new Bit8(), new Bit8());}}
	public static class Bit23 extends BitN {public Bit23(int value) {this();set(value);}public Bit23() {super(new Bit7(), new Bit8(), new Bit8());}}
	public static class Bit24 extends BitN {public Bit24(int value) {this();set(value);}public Bit24() {super(new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit25 extends BitN {public Bit25(int value) {this();set(value);}public Bit25() {super(new Bit1(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit26 extends BitN {public Bit26(int value) {this();set(value);}public Bit26() {super(new Bit2(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit27 extends BitN {public Bit27(int value) {this();set(value);}public Bit27() {super(new Bit3(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit28 extends BitN {public Bit28(int value) {this();set(value);}public Bit28() {super(new Bit4(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit29 extends BitN {public Bit29(int value) {this();set(value);}public Bit29() {super(new Bit5(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit30 extends BitN {public Bit30(int value) {this();set(value);}public Bit30() {super(new Bit6(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit31 extends BitN {public Bit31(int value) {this();set(value);}public Bit31() {super(new Bit7(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit32 extends BitN {public Bit32(int value) {this();set(value);}public Bit32() {super(new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit33 extends BitN {public Bit33(int value) {this();set(value);}public Bit33() {super(new Bit1(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit34 extends BitN {public Bit34(int value) {this();set(value);}public Bit34() {super(new Bit2(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit35 extends BitN {public Bit35(int value) {this();set(value);}public Bit35() {super(new Bit3(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit36 extends BitN {public Bit36(int value) {this();set(value);}public Bit36() {super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit37 extends BitN {public Bit37(int value) {this();set(value);}public Bit37() {super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit38 extends BitN {public Bit38(int value) {this();set(value);}public Bit38() {super(new Bit6(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit39 extends BitN {public Bit39(int value) {this();set(value);}public Bit39() {super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit40 extends BitN {public Bit40(int value) {this();set(value);}public Bit40() {super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit41 extends BitN {public Bit41(int value) {this();set(value);}public Bit41() {super(new Bit1(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit42 extends BitN {public Bit42(int value) {this();set(value);}public Bit42() {super(new Bit2(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit43 extends BitN {public Bit43(int value) {this();set(value);}public Bit43() {super(new Bit3(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit44 extends BitN {public Bit44(int value) {this();set(value);}public Bit44() {super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit45 extends BitN {public Bit45(int value) {this();set(value);}public Bit45() {super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit46 extends BitN {public Bit46(int value) {this();set(value);}public Bit46() {super(new Bit6(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit47 extends BitN {public Bit47(int value) {this();set(value);}public Bit47() {super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit48 extends BitN {public Bit48(int value) {this();set(value);}public Bit48() {super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit49 extends BitN {public Bit49(int value) {this();set(value);}public Bit49() {super(new Bit1(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit50 extends BitN {public Bit50(int value) {this();set(value);}public Bit50() {super(new Bit2(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit51 extends BitN {public Bit51(int value) {this();set(value);}public Bit51() {super(new Bit3(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit52 extends BitN {public Bit52(int value) {this();set(value);}public Bit52() {super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit53 extends BitN {public Bit53(int value) {this();set(value);}public Bit53() {super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit54 extends BitN {public Bit54(int value) {this();set(value);}public Bit54() {super(new Bit6(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit55 extends BitN {public Bit55(int value) {this();set(value);}public Bit55() {super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit56 extends BitN {public Bit56(int value) {this();set(value);}public Bit56() {super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit57 extends BitN {public Bit57(int value) {this();set(value);}public Bit57() {super(new Bit1(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit58 extends BitN {public Bit58(int value) {this();set(value);}public Bit58() {super(new Bit2(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit59 extends BitN {public Bit59(int value) {this();set(value);}public Bit59() {super(new Bit3(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit60 extends BitN {public Bit60(int value) {this();set(value);}public Bit60() {super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit61 extends BitN {public Bit61(int value) {this();set(value);}public Bit61() {super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit62 extends BitN {public Bit62(int value) {this();set(value);}public Bit62() {super(new Bit6(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit63 extends BitN {public Bit63(int value) {this();set(value);}public Bit63() {super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
	public static class Bit64 extends BitN {public Bit64(int value) {this();set(value);}public Bit64() {super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());}}
}
