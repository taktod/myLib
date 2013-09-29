package com.ttProject.media.flv.model;

import java.nio.ByteBuffer;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvManager;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;
import com.ttProject.util.HexUtil;

/**
 * FLVの内容解析、ただしデータの読み込みは実行しない。
 * @author taktod
 */
public class TagPositionAnalyzer implements ITagAnalyzer {
	private final FlvManager manager = new FlvManager();
	@Override
	public Tag analyze(IReadChannel ch) throws Exception {
		Tag tag = null;
		do {
			if(tag != null) {
				ch.position(tag.getPosition() + tag.getInitSize());
			}
			tag = manager.getUnit(ch);
			if(tag == null) {
				return null;
			}
		}
		while((tag instanceof VideoTag || tag instanceof AudioTag) && tag.getSize() <= 15);
		if(tag instanceof VideoTag) {
			// とりあえずmshもほしい。
			// indexもほしい。
			// mshについては複数はいる可能性が一応ある。
			/*
			 * よって作成する動作は次のようにしたい。
			 * mshの情報位置設定
			 * keyFrameの位置設定
			 * mshについては、audioもありうるので、注意が必要
			 */
			VideoTag vTag = (VideoTag) tag;
			ByteBuffer checkBuffer = BufferUtil.safeRead(ch, 2);
			// keyframeかしっておきたいので、この方法はまずい
			byte check = checkBuffer.get();
//			System.out.println(check);
			if(vTag.analyzeTagByte(check)) {
				// avcなのでmshの判定が必要
				vTag.setMSHFlg(checkBuffer.get() == 0x00);
			}
		}
		else if(tag instanceof AudioTag) {
			AudioTag aTag = (AudioTag) tag;
			ByteBuffer checkBuffer = BufferUtil.safeRead(ch, 2);
			byte check = checkBuffer.get();
			if(aTag.analyzeTagByte(check)) {
				aTag.setMSHFlg(checkBuffer.get() == 0x00);
			}
		}
		// ほぼこっちで飛ばしが発生しているとみてよさそう。
		// とりあえずanalyzeだけせずに動作させてみる。
		// これが一番はやいっぽい。
		ch.position(tag.getPosition() + tag.getInitSize());
		return tag;
	}
}
