package com.ttProject.media.h264;

import java.nio.ByteBuffer;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit5;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

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
public abstract class Frame extends Unit {
	private Bit1 forbiddenZeroBit; // 0のみ?
	private Bit2 nalRefIdc; // 0:ならなくてもいいやつ?数値のあるやつはdecodeに必須なnalなお0x09のadtはmpegtsには必要っぽい。
	private Bit5 type; // typeで宣言している数値がはいるっぽい
	private ByteBuffer buffer; // データ本体保持
	public Frame(final int size, byte frameTypeData) {
		super(0, size);
		forbiddenZeroBit = new Bit1(frameTypeData >>> 7);
		nalRefIdc = new Bit2(frameTypeData >>> 5);
		type = new Bit5(frameTypeData);
	}
	// たぶんつかわん
	public int getForbiddenZeroBit() {
		return forbiddenZeroBit.get();
	}
	public int getNalRefIdc() {
		return nalRefIdc.get();
	}
	public int getType() {
		return type.get();
	}
	public ByteBuffer getBuffer() {
		return buffer;
	}
	public ByteBuffer getData() throws Exception {
		ByteBuffer data = ByteBuffer.allocate(buffer.remaining() + 1);
		data.put(Bit.bitConnector(forbiddenZeroBit, nalRefIdc, type));
		data.put(buffer);
		buffer.position(0);
		data.flip();
		return data;
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
		if(analyzer == null){
			analyze(ch, (IFrameAnalyzer)null);
		}
	}
	public void analyze(IReadChannel ch, IFrameAnalyzer analyzer) throws Exception {
		// 設定されている分だけデータを読み込む
		if(getSize() <= 1) {
			throw new Exception("解析実行する前にデータサイズが設定されていません。");
		}
		if(ch.size() - ch.position() < getSize() - 1) {
			throw new Exception("読み込みに必要なデータがありません。");
		}
		buffer = BufferUtil.safeRead(ch, getSize() - 1);
	}
}
