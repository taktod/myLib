package com.ttProject.media.mkv.element;

import java.nio.ByteBuffer;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 音声と映像の実データを保持しているっぽい
 * [タグ EBMLの長さ]はほかと同じ
 * EBML伸縮数値によるTrackID 2バイトtimecode フラグ
 * フラグはAAAABCCDとなっているっぽい
 * AAAAは0固定
 * Bは見えないフレーム(DisposableInnerっぽいやつ？)
 * CCはlacingのフラグ
 * Dは利用しません。0固定か？
 * その後のデータは普通にFrameデータでした。
 * @author taktod
 *
 */
public class Block extends Element {
	public Block(long position, long size, long dataPosition) {
		super(Type.Block, position, size, dataPosition);
	}
	public Block(IReadChannel ch) throws Exception {
		this(ch.position() - Type.Block.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
		// blockの内容を解析します。
		ch.position((int)getDataPosition());
		// trackId取得
		int trackId = (int)getEbmlNumber(ch);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 3);
		int timeDiff = buffer.getShort();
		byte flg = buffer.get();
		// flgを確認する(とりあえずしない、lacingのみ調べておく。)
		byte lacingType = (byte)((flg >> 1) & 0x03);
		switch(lacingType) {
		case 0:
			System.out.println("no lacing");
			break;
		case 1:
			{
				System.out.println("Xiph lacing");
				int laceCount = BufferUtil.safeRead(ch, 1).get();
				for(int i = 0;i < laceCount;i ++) {
					int size = 0;
					int newDat = 0;
					while((newDat = (BufferUtil.safeRead(ch, 1).get() & 0xFF)) == 0xFF) {
						size += 0xFF;
					}
					size += newDat;
					System.out.println(Integer.toHexString(size));
				}
			}
			break;
		case 2:
			{
				System.out.println("fixed-size lacing");
				int laceCount = BufferUtil.safeRead(ch, 1).get();
				int totalSize = (int)(getSize() - ch.position() + getDataPosition());
				int laceSize = totalSize / (laceCount + 1);
				for(int i = 0;i < laceCount;i ++) {
					System.out.println(Integer.toHexString(laceSize));
				}
			}
			break;
		case 3:
			{
				System.out.println("ebml lacing");
				// 次の１バイトがいくつlacingしているか示している。
				int laceCount = BufferUtil.safeRead(ch, 1).get();
				System.out.println(laceCount);
				int laceSize = (int)getEbmlNumber(ch);
				System.out.println(Integer.toHexString(laceSize));
				laceCount --;
				while(laceCount > 0) {
					int nextLaceDiff = (int)getEbmlNumber(ch);
					if(nextLaceDiff <= 0x7f) {
						laceSize += nextLaceDiff - 0x3F;
					}
					else if(nextLaceDiff <= 0x3FFF){
						laceSize += nextLaceDiff - 0x1FFF;
					}
					else if(nextLaceDiff <= 0x1FFFFF) {
						laceSize += nextLaceDiff - 0x0FFFFF;
					}
					else if(nextLaceDiff <= 0x0FFFFFFF) {
						laceSize += nextLaceDiff - 0x07FFFFFF;
					}
					System.out.println(Integer.toHexString(laceSize));
					laceCount --;
				}
			}
			break;
		}
		System.out.println(flg);
		System.out.println(trackId);
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
