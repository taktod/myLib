package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvBinaryTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;

/**
 * SimpleBlockタグ
 * データは次のようになっています。
 * A3 44 B4 81 00 00 80 00 00 02 6C ...
 *  A3[SimpleBlockタグ]
 *  44 B4[このTagのサイズデータ]
 * ここまでは読み込み動作実装済み
 *  81[EbmlValue] 動作トラックデータ
 *  00 00[16bit固定]このCluster上でのtimestamp差分量
 *  1000 0000
 *  . keyFrameであるか指定
 *   ... reserved0設定
 *       . 非表示フレームであるか？ 1なら非表示
 *        .. lacing設定(データが複数のフレームの塊の場合にどのようにわかれるかの指定がはいっている*1)
 *          . discardable:なんだろう？
 * *1:h264のnalはフレームの塊ではあるけど、lacingではなくnal構造で分かれるようになっています。
 * @see http://matroska.org/technical/specs/index.html#simpleblock_structure
 * @author taktod
 */
public class SimpleBlock extends MkvBinaryTag {
	private EbmlValue trackId            = new EbmlValue();
	private Bit16     timestampDiff      = new Bit16();
	private Bit1      keyFrameFlag       = new Bit1();
	private Bit3      reserved           = new Bit3();
	private Bit1      invisibleFrameFlag = new Bit1();
	private Bit2      lacing             = new Bit2();
	private Bit1      discardableFlag    = new Bit1();
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SimpleBlock(EbmlValue size) {
		super(Type.SimpleBlock, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(trackId, timestampDiff, keyFrameFlag, reserved, invisibleFrameFlag, lacing, discardableFlag);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getRemainedSize() {
		return getMkvSize() - (trackId.getBitCount() + 24) / 8;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(super.toString());
		data.append(" trackId:").append(trackId.get());
		return data.toString();
	}
}
