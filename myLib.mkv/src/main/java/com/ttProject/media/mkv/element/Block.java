package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

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
	public Block(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.Block.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
