package com.ttProject.media.h264;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit5;
import com.ttProject.nio.channels.IReadChannel;

/**
 * nalの基本構造
 * @see http://r2d2n3po.tistory.com/26
 * @see http://www.itu.int/rec/T-REC-H.264-201304-I/en
 * 0               1               2               3
 * 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  |F|NRI|  Type   |R|I|   PRID    |N| DID |  QID  | TID |U|D|O| RR|
  mshの部分は次のようになっているらしい。
  01:avcC version1
  spsのindex 1,2,3の部分(profile, compatibilityFlg, levelがはいっている)
  ff
  e1:spsの数値?
  xx xx spsの長さ
  spsの実データ
  01:ppsの数値?
  xx xx ppsの長さ
  ppsの実データ
  
  その後のflvタグは次のようになっているみたい。
  09 size[3byte] timestamp[4byte(転地あり)] trackId[3byte(0埋め)] コーデックタイプとフレームフラグ(1byte)
  mshFlag(00:msh 01:通常フレーム) avcCompositionTimeOffset[3byte] nalの内部データサイズ[4バイト] nalの実データ
  tailsize[4byte]
  となっている模様
 * @author taktod
 */
public abstract class Nal extends Unit {
	private Bit1 forbiddenZeroBit; // 0のみ?
	private Bit2 nalRefIdc; // 0:ならなくてもいいやつ?
	private Bit5 type; // typeで宣言している数値がはいるっぽい
	public Nal(final int position, final int size) {
		super(position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
	public abstract void analyze(IReadChannel ch, INalAnalyzer analyzer) throws Exception;
}
