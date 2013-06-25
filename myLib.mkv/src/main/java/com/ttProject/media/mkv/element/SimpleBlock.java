package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 音声の実データや映像の実データを保持しているみたい。
 * [タグ EBMLの伸縮する長さ]
 * EBMLの伸縮する数値によるtrackID 2バイトClusterのtimecodeからのtimestamp相対位置 フラグ
 * フラグはABBBCDDEとビットにわかれる。
 * A:KeyFrameが含まれるときに、フラグがたつ
 * B:0固定
 * C:1がはいっている場合はデコードすべきフレームだが、表示はされない。(H.263のdisposableInnerみたいなものか？)
 * D:lacing用のフラグ(なにかわからん)
 * E:discardable(これもdisposableInnerと同じっぽい。不明)
 * このあとに続くデータは通常のデータフレームだった
 * @author taktod
 *
 */
public class SimpleBlock extends Element {
	public SimpleBlock(long position, long size, long dataPosition) {
		super(Type.SimpleBlock, position, size, dataPosition);
	}
	public SimpleBlock(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.SimpleBlock.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
